package machura.przemyslaw.mobees.ticketsmanagement.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods.AdjustReducedTicketsRequest;
import machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods.CreateQuarterlyRequest;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsAdjuster;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.SalesPeriodsPersistenceService;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SalesPeriodsService {

    // todo to clarify
    private static final int SINGLE_MATCH_TOTAL_TICKET_POOL = 50;
    private static final DayOfWeek GAME_DAY = DayOfWeek.TUESDAY;

    private final SalesPeriodsPersistenceService persistenceService;
    private final TimeProvider timeProvider;

    public Either<Failure, SalesPeriod> create(CreateQuarterlyRequest request) {
        return persistenceService.findByQuarterRange(LocalDate.now())
                .flatMap(salesPeriods -> salesPeriods.isEmpty() ?
                        createQuarterlySalesPeriod(request.getReducedTicketsPool())
                        : Either.left(Failure.from("Sales period already exists")));
    }

    public Either<Failure, SalesPeriod> adjustTicketsForQuarterlySalesPeriod(AdjustReducedTicketsRequest request) {
        return persistenceService.findByQuarterRange(request.getSalesPeriodDate())
                .flatMap(salesPeriods -> salesPeriods.isEmpty() ?
                        Either.left(Failure.from("Sales period does not exists"))
                        :
                        getProperTicketsAdjuster(request)
                                .map(ticketsAdjuster -> salesPeriods.get(0).adjustTickets(ticketsAdjuster, timeProvider)
                                        .flatMap(success -> persistenceService.updateAll(salesPeriods.get(0))))
                                .orElseGet(() -> Either.left(Failure.from("Bad reduced tickets change " + request.getReducedTicketsChange()))));
    }

    public Either<Failure, Optional<SalesPeriod>> findQuarterlySalesPeriod(LocalDate localDate) {
        return persistenceService.findByQuarterRange(localDate)
                .flatMap(salesPeriods -> salesPeriods.size() > 1 ?
                        Either.left(Failure.from(String.format("Illegal server state. %d sales periods found.", salesPeriods.size())))
                        :
                        Try.ofSupplier(() -> salesPeriods.get(0))
                                .map(found -> Either.<Failure, Optional<SalesPeriod>>right(Optional.of(found)))
                                .getOrElseGet(emptyList -> Either.right(Optional.empty())));
    }

    private Either<Failure, SalesPeriod> createQuarterlySalesPeriod(int reducedTicketsPool) {
        SalesPeriodSpecQuarterly createSalesPeriodQuarterly = SalesPeriodSpecQuarterly.from(
                LocalDate.now(), GAME_DAY, SINGLE_MATCH_TOTAL_TICKET_POOL, reducedTicketsPool
        );

        SalesPeriod quarterly = SalesPeriod.create(
                createSalesPeriodQuarterly,
                new MatchPlanningPolicyQuarterly(createSalesPeriodQuarterly),
                new TicketsDistributorQuarterly(createSalesPeriodQuarterly, timeProvider)
        );

        return persistenceService.createAll(quarterly);
    }

    private Optional<? extends TicketsAdjuster> getProperTicketsAdjuster(AdjustReducedTicketsRequest request) {
        int reducedTicketChange = request.getReducedTicketsChange();
        if (reducedTicketChange == 0) return Optional.empty();

        return reducedTicketChange > 0 ?
                ReducedTicketsIncrement.create(timeProvider, reducedTicketChange) : ReducedTicketsDecrement.create(timeProvider, reducedTicketChange);
    }
}
