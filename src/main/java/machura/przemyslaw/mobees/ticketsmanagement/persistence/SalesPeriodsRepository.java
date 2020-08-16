package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.SalesPeriodDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesPeriodsRepository extends JpaRepository<SalesPeriodDAO, Long> {
    @Query("SELECT sp FROM SalesPeriodDAO sp WHERE sp.startDate <= :date and sp.endDate >= :date")
    List<SalesPeriodDAO> findByQuarterRange(@Param("date") LocalDate date);
}
