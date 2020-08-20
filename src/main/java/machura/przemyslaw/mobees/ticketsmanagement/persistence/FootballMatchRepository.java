package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FootballMatchRepository extends JpaRepository<FootballMatchDAO, Long> {
    void deleteByIdIn(Collection<Long> ids);
}
