package machura.przemyslaw.mobees.ticketsmanagement.persistence.mappers;

import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.TicketsSpec;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchTicketsDAO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FootballMatchTicketsMapper {
    public static FootballMatchTicketsDAO map(FootballMatchTickets footballMatchTickets) {
        return FootballMatchTicketsDAO.builder()
                .id(footballMatchTickets.getId())
                .footballMatch(FootballMatchMapper.map(footballMatchTickets.getFootballMatch()))
                .totalTicketsPool(footballMatchTickets.getTotalTicketsPool())
                .reducedTicketsPool(footballMatchTickets.getReducedTicketsPool())
                .reservedTickets(footballMatchTickets.getReservedTickets())
                .build();
    }

    public static List<FootballMatchTicketsDAO> map(Collection<? extends FootballMatchTickets> footballMatchesTickets) {
        return footballMatchesTickets.stream()
                .map(FootballMatchTicketsMapper::map)
                .collect(Collectors.toList());
    }

    public static List<FootballMatchTickets> fromDAOSToDomains(Collection<? extends FootballMatchTicketsDAO> footballMatchTicketsDAOS) {
        return footballMatchTicketsDAOS.stream()
                .map(FootballMatchTicketsMapper::fromDAOToDomain)
                .collect(Collectors.toList());
    }

    public static FootballMatchTickets fromDAOToDomain(FootballMatchTicketsDAO dao) {
        return FootballMatchTickets.builder()
                .id(dao.getId())
                .footballMatch(FootballMatchMapper.map(dao.getFootballMatch()))
                .ticketsSpec(TicketsSpec.builder()
                        .totalTicketsPool(dao.getTotalTicketsPool())
                        .reducedTicketsPool(dao.getReducedTicketsPool())
                        .reservedTickets(dao.getReservedTickets())
                        .buildWithoutValidation())
                .build();
    }
}
