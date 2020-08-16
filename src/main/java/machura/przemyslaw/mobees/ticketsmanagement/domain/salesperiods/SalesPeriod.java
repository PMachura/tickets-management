package machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods;

import io.vavr.control.Either;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.common.Success;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.MatchPlanningPolicy;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsAdjuster;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsDistributor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Builder
@Getter
public class SalesPeriod {
    private final Long id;
    private final LocalDate createdAt;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<FootballMatchTickets> footballMatchTickets;

    public static SalesPeriod create(SalesPeriodSpec salesPeriodSpec,
                                     MatchPlanningPolicy matchPlanningPolicy,
                                     TicketsDistributor ticketsDistributor) {
        return SalesPeriod.builder()
                .createdAt(salesPeriodSpec.requestDate())
                .startDate(salesPeriodSpec.startDate())
                .endDate(salesPeriodSpec.endDate())
                .footballMatchTickets(ticketsDistributor.distributeTickets(matchPlanningPolicy.plan()))
                .build();
    }

    public Either<Failure, Success> adjustTickets(TicketsAdjuster ticketsAdjuster,
                                                  TimeProvider timeProvider){
        if(isOpen(timeProvider)){
            ticketsAdjuster.adjust(footballMatchTickets);
            return Either.right(Success.from("Tickets adjustment performed"));
        }

        return Either.left(Failure.from("Sales period closed"));
    }

    public boolean isOpen(TimeProvider timeProvider){
        LocalDate now = timeProvider.now();
        return now.isEqual(startDate)
                || now.isEqual(endDate)
                || (now.isAfter(startDate) && now.isBefore(endDate));
    }

    public int matchesWithOpenSale(TimeProvider timeProvider){
        return (int) footballMatchTickets.stream()
                .filter(matTickets -> matTickets.isSaleOpen(timeProvider))
                .count();
    }

    public int matchesWithClosedSale(TimeProvider timeProvider){
        return (int) footballMatchTickets.stream()
                .filter(matTickets -> matTickets.isSaleClosed(timeProvider))
                .count();
    }

    public int matchesWhereTotalTicketsPoolIsEmpty(){
        return (int) footballMatchTickets.stream()
                .filter(FootballMatchTickets::isTotalTicketPoolEmpty)
                .count();
    }

    public int reducedTicketsPool(){
        return footballMatchTickets.stream()
                .map(FootballMatchTickets::getReducedTicketsPool)
                .reduce(0, Integer::sum);
    }

    public int totalTicketsPool(){
        return footballMatchTickets.stream()
                .map(FootballMatchTickets::getTotalTicketsPool)
                .reduce(0, Integer::sum);
    }

    public int reservedTickets(){
        return footballMatchTickets.stream()
                .map(FootballMatchTickets::getReservedTickets)
                .reduce(0, Integer::sum);
    }
}
