package machura.przemyslaw.mobees.ticketsmanagement.domain.tickets;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
@ToString
public class FootballMatchTickets {
    private final Long id;
    private final FootballMatch footballMatch;
    private final TicketsSpec ticketsSpec;

    public Long getId() {
        return id;
    }

    public FootballMatch getFootballMatch() {
        return footballMatch;
    }

    public static List<FootballMatchTickets> sortByMatchDate(Collection<? extends FootballMatchTickets> matchTickets){
        return matchTickets.stream()
                .sorted(Comparator.comparing(FootballMatchTickets::getMatchDate))
                .collect(Collectors.toList());
    }

    public int incrementReducedTicketsPool(int requestedIncrementValue){
        return ticketsSpec.incrementReducedTicketsPoolAndReturnDiff(requestedIncrementValue);
    }

    public int decrementReducedTicketsPool(int requestedDecreaseValue){
        return ticketsSpec.decrementReducedTicketsPoolAndReturnDiff(requestedDecreaseValue);
    }

    public LocalDate getMatchDate(){
        return footballMatch.getMatchDate();
    }

    public boolean isTotalTicketPoolEmpty(){
        return getTotalTicketsPool() == 0;
    }

    public boolean isSaleOpen(TimeProvider timeProvider){
        return footballMatch.isInPlanningPhase(timeProvider);
    }

    public boolean isSaleClosed(TimeProvider timeProvider){
        return footballMatch.isPast(timeProvider);
    }

    public int getTotalTicketsPool() {
        return ticketsSpec.getTotalTicketsPool();
    }

    public int getReducedTicketsPool() {
        return ticketsSpec.getReducedTicketsPool();
    }

    public int getReservedTickets() {
        return ticketsSpec.getReservedTickets();
    }
}
