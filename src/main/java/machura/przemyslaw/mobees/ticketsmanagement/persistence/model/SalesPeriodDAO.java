package machura.przemyslaw.mobees.ticketsmanagement.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Table(name = "sales_periods")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SalesPeriodDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate createdAt;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;

    @OneToMany(mappedBy = "salesPeriod", fetch = FetchType.EAGER)
    private List<FootballMatchTicketsDAO> footballMatchTickets;
}
