package machura.przemyslaw.mobees.ticketsmanagement.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.MatchPlanningPolicy;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriodSpec;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * todo There are many assumptions, which should be dispel
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MatchPlanningPolicyQuarterly implements MatchPlanningPolicy {

    private final SalesPeriodSpec salesPeriodSpec;

    @Override
    public List<FootballMatch> plan() {
        LocalDate firstMatchDay = getFirstGameDate(salesPeriodSpec.startDate());
        LocalDate lastMatchDay = getLastGameDate(salesPeriodSpec.endDate());
        int totalMatchWeeks = (int) ChronoUnit.WEEKS.between(firstMatchDay, lastMatchDay) + 1;

        return IntStream.range(0, totalMatchWeeks)
                .mapToObj(matchWeek -> FootballMatch.builder()
                        .matchDate(firstMatchDay.plusWeeks(matchWeek))
                        .build())
                .collect(Collectors.toList());
    }

    private LocalDate getFirstGameDate(LocalDate startDate) {
        return startDate.with(TemporalAdjusters.nextOrSame(salesPeriodSpec.matchDay()));
    }

    private LocalDate getLastGameDate(LocalDate endDate) {
        return endDate.with(TemporalAdjusters.previousOrSame(salesPeriodSpec.matchDay()));
    }
}
