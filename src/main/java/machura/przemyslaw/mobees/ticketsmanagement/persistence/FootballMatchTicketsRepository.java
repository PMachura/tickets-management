package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchTicketsDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FootballMatchTicketsRepository extends JpaRepository<FootballMatchTicketsDAO, Long> {
    void deleteByIdIn(Collection<Long> ids);
}
