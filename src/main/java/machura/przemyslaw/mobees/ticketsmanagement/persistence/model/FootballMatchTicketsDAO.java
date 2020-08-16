package machura.przemyslaw.mobees.ticketsmanagement.persistence.model;

import lombok.*;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsSpec;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "football_match_tickets")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FootballMatchTicketsDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(0)
    private Integer totalTicketsPool;

    @Min(0)
    private Integer reducedTicketsPool;

    @Min(0)
    private Integer reservedTickets;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "football_match_id", referencedColumnName = "id", nullable = false)
    private FootballMatchDAO footballMatch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sales_period_id", nullable = true)
    private SalesPeriodDAO salesPeriod;
}
