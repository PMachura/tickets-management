package machura.przemyslaw.mobees.ticketsmanagement.domain.matches;

import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;

import java.util.List;

public interface MatchPlanningPolicy {
    List<FootballMatch> plan();
}
