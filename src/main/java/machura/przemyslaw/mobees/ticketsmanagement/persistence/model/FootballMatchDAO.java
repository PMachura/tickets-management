package machura.przemyslaw.mobees.ticketsmanagement.persistence.model;

import lombok.*;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "football_matches")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FootballMatchDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate matchDate;

    @OneToOne(mappedBy = "footballMatch", fetch = FetchType.LAZY)
    private FootballMatchTicketsDAO footballMatchTickets;

}
