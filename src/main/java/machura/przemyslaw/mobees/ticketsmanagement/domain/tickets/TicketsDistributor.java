package machura.przemyslaw.mobees.ticketsmanagement.domain.tickets;

import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;

import java.util.Collection;
import java.util.List;

public interface TicketsDistributor {
    List<FootballMatchTickets> distributeTickets(Collection<? extends FootballMatch> footballMatches);
}
