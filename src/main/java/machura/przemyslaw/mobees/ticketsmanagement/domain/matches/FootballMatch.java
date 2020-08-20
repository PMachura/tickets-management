package machura.przemyslaw.mobees.ticketsmanagement.domain.matches;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
@ToString
public class FootballMatch {
    private final Long id;
    private final LocalDate matchDate;

    public boolean isPast(LocalDate localDate) {
        return matchDate.isBefore(localDate) || matchDate.isEqual(localDate);
    }

    public boolean isInPlanningPhase(LocalDate localDate) {
        return matchDate.isAfter(localDate);
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public Long getId() {
        return id;
    }

    public static List<FootballMatch> sortByMatchDate(Collection<? extends FootballMatch> matches) {
        return matches.stream()
                .sorted(Comparator.comparing(match -> match.getMatchDate()))
                .collect(Collectors.toList());
    }
}
