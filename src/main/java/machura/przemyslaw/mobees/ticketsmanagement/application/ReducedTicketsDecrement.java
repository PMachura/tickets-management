package machura.przemyslaw.mobees.ticketsmanagement.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.common.Utils;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsAdjuster;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class ReducedTicketsDecrement implements TicketsAdjuster {

    private final int reducedTicketDecrement;
    private final TimeProvider timeProvider;

    public static Optional<ReducedTicketsDecrement> create(TimeProvider timeProvider,
                                                           int reducedTicketsChange) {
        return reducedTicketsChange < 0 ?
                Optional.of(new ReducedTicketsDecrement(-reducedTicketsChange, timeProvider)) : Optional.empty();
    }

    // todo consider to return new objects without modifying given collection
    @Override
    public List<FootballMatchTickets> adjust(Collection<? extends FootballMatchTickets> matchTickets) {
        List<FootballMatchTickets> sortedWithOpenSale = matchTickets.stream()
                .filter(footballMatchTickets -> footballMatchTickets.isSaleOpen(timeProvider))
                .sorted(FootballMatchTickets.byReducedTicketsPoolComparator.reversed()
                        .thenComparing(FootballMatchTickets.byMatchDateComparator))
                .collect(Collectors.toList());

        List<Integer> decrementPerMatch = Utils.splitEvenlyWithReminder(reducedTicketDecrement, sortedWithOpenSale.size());

        IntStream.range(0, sortedWithOpenSale.size())
                .forEach(matchNumber -> sortedWithOpenSale.get(matchNumber)
                        .decrementReducedTicketsPool(decrementPerMatch.get(matchNumber))
                );

        return FootballMatchTickets.sortByMatchDate(matchTickets);
    }
}
