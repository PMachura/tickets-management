package machura.przemyslaw.mobees.ticketsmanagement.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.common.Utils;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriodSpec;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsDistributor;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsSpec;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TicketsDistributorQuarterly implements TicketsDistributor {

    private final SalesPeriodSpec salesPeriodSpec;
    private final TimeProvider timeProvider;

    @Override
    public List<FootballMatchTickets> distributeTickets(Collection<? extends FootballMatch> footballMatches) {
        List<FootballMatch> sortedMatches = FootballMatch.sortByMatchDate(footballMatches);

        List<FootballMatch> pastMatches = sortedMatches.stream().filter(match -> match.isPast(timeProvider)).collect(Collectors.toList());
        List<FootballMatch> plannedMatches = sortedMatches.stream().filter(match -> match.isInPlanningPhase(timeProvider)).collect(Collectors.toList());

        List<FootballMatchTickets> ticketsForPastMatches = distributeTicketsForPastMatches(pastMatches);
        List<FootballMatchTickets> ticketsForPlannedMatches = distributeTicketsForPlannedMatches(
                plannedMatches,
                salesPeriodSpec.singleMatchTotalTicketsPool(),
                salesPeriodSpec.reducedTicketsPool()
        );

        return Stream.concat(ticketsForPastMatches.stream(), ticketsForPlannedMatches.stream())
                .collect(Collectors.toList());
    }

    private List<FootballMatchTickets> distributeTicketsForPastMatches(List<FootballMatch> pastMatches) {
        return pastMatches.stream()
                .map(pastMatch -> FootballMatchTickets.builder().footballMatch(pastMatch)
                        .ticketsSpec(TicketsSpec.withEmptyTicketsPool())
                        .build())
                .collect(Collectors.toList());
    }

    private List<FootballMatchTickets> distributeTicketsForPlannedMatches(List<FootballMatch> plannedMatches,
                                                                          int singleMatchTotalTicketPool,
                                                                          int reducedTicketsPool) {

        List<Integer> reducedTicketsPerMatch = Utils.splitEvenlyWithReminder(reducedTicketsPool, plannedMatches.size());
        return IntStream.range(0, plannedMatches.size()).boxed()
                .map(plannedMatchNumber ->
                        FootballMatchTickets.builder()
                                .footballMatch(plannedMatches.get(plannedMatchNumber))
                                .ticketsSpec(
                                        TicketsSpec.builder()
                                                .totalTicketsPool(singleMatchTotalTicketPool)
                                                .reducedTicketsPool(Math.min(
                                                        reducedTicketsPerMatch.get(plannedMatchNumber),
                                                        singleMatchTotalTicketPool
                                                ))
                                                .build()
                                                .orElseGet(TicketsSpec::withEmptyTicketsPool))
                                .build())
                .collect(Collectors.toList());
    }
}
