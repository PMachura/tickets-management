package machura.przemyslaw.mobees.ticketsmanagement.persistence.mappers;

import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchDAO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FootballMatchMapper {
    public static FootballMatchDAO map(FootballMatch footballMatch){
        return FootballMatchDAO.builder()
                .id(footballMatch.getId())
                .matchDate(footballMatch.getMatchDate())
                .build();
    }

    public static List<FootballMatchDAO> map(Collection<? extends FootballMatch> footballMatches){
        return footballMatches.stream()
                .map(FootballMatchMapper::map)
                .collect(Collectors.toList());
    }

    public static FootballMatch map(FootballMatchDAO dao){
        return FootballMatch.builder()
                .id(dao.getId())
                .matchDate(dao.getMatchDate())
                .build();
    }
}
