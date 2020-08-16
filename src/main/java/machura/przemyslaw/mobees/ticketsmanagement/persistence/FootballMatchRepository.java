package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootballMatchRepository extends JpaRepository<FootballMatchDAO, Long> {
}
