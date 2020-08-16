package machura.przemyslaw.mobees.ticketsmanagement.domain.tickets;

import java.util.Collection;
import java.util.List;

public interface TicketsAdjuster {
    List<FootballMatchTickets> adjust(Collection<? extends FootballMatchTickets> matchTickets);
}
