package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchTicketsDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootballMatchTicketsRepository extends JpaRepository<FootballMatchTicketsDAO, Long> {
}
