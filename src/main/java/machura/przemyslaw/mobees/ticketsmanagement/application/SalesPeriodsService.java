package machura.przemyslaw.mobees.ticketsmanagement.application;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods.AdjustReducedTicketsRequest;
import machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods.CreateQuarterlyRequest;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.common.Success;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsAdjuster;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.SalesPeriodsPersistenceService;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
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
        LocalDate salesPeriodDate = request.getSalesPeriodDate().orElseGet(() -> timeProvider.now());
        return persistenceService.findByQuarterRange(salesPeriodDate)
                .flatMap(salesPeriods -> salesPeriods.isEmpty() ?
                        createQuarterlySalesPeriod(salesPeriodDate, request.getReducedTicketsPool())
                        : Either.left(Failure.from("Sales period already exists", Failure.Status.ILLEGAL_INPUT)));
    }

    public Either<Failure, Success> deleteQuarterlySalesPeriod(LocalDate localDate) {
        return persistenceService.findByQuarterRange(localDate)
                .flatMap(this::validateFoundQuarterlyAndGet)
                .flatMap(persistenceService::deleteAll);
    }

    public Either<Failure, SalesPeriod> adjustTicketsForQuarterlySalesPeriod(AdjustReducedTicketsRequest request) {
        return persistenceService.findByQuarterRange(request.getSalesPeriodDate())
                .flatMap(this::validateFoundQuarterlyAndGet)
                .flatMap(salesPeriod -> adjustTicketsForQuarterlySalesPeriod(request, salesPeriod));
    }


    public Either<Failure, SalesPeriod> findQuarterlySalesPeriod(LocalDate localDate) {
        return persistenceService.findByQuarterRange(localDate)
                .flatMap(this::validateFoundQuarterlyAndGet);
    }

    private Either<Failure, SalesPeriod> createQuarterlySalesPeriod(LocalDate date, int reducedTicketsPool) {
        SalesPeriodSpecQuarterly createSalesPeriodQuarterly = SalesPeriodSpecQuarterly.from(
                date, GAME_DAY, SINGLE_MATCH_TOTAL_TICKET_POOL, reducedTicketsPool
        );

        SalesPeriod quarterly = SalesPeriod.create(
                createSalesPeriodQuarterly,
                new MatchPlanningPolicyQuarterly(createSalesPeriodQuarterly),
                new TicketsDistributorQuarterly(createSalesPeriodQuarterly)
        );

        return persistenceService.createAll(quarterly);
    }

    private Either<Failure, SalesPeriod> validateFoundQuarterlyAndGet(Collection<SalesPeriod> salesPeriods){
        if(salesPeriods.isEmpty()) return Either.left(Failure.from("Not found", Failure.Status.RESOURCE_NOT_FOUND));
        if(salesPeriods.size() > 1) return Either.left(Failure.from(String.format("Illegal server state. %d sales periods found.", salesPeriods.size()), Failure.Status.SERVER_ERROR));
        return Either.right(salesPeriods.stream().findFirst().get());
    }

    private Either<Failure, SalesPeriod> adjustTicketsForQuarterlySalesPeriod(AdjustReducedTicketsRequest request, SalesPeriod salesPeriod){
        return getProperTicketsAdjuster(request)
                .map(ticketsAdjuster -> salesPeriod.adjustTickets(ticketsAdjuster, timeProvider)
                        .flatMap(success -> persistenceService.updateAll(salesPeriod)))
                .orElseGet(() -> Either.left(Failure.from("Bad reduced tickets change " + request.getReducedTicketsChange(), Failure.Status.ILLEGAL_INPUT)));
    }

    private Optional<? extends TicketsAdjuster> getProperTicketsAdjuster(AdjustReducedTicketsRequest request) {
        int reducedTicketChange = request.getReducedTicketsChange();
        if (reducedTicketChange == 0) return Optional.empty();

        return reducedTicketChange > 0 ?
                ReducedTicketsIncrement.create(timeProvider, reducedTicketChange) : ReducedTicketsDecrement.create(timeProvider, reducedTicketChange);
    }
}
