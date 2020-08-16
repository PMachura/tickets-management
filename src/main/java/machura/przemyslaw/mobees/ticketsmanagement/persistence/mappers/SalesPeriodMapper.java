package machura.przemyslaw.mobees.ticketsmanagement.persistence.mappers;

import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.SalesPeriodDAO;

public class SalesPeriodMapper {
    public static SalesPeriodDAO map(SalesPeriod salesPeriod) {
        return SalesPeriodDAO.builder()
                .id(salesPeriod.getId())
                .footballMatchTickets(FootballMatchTicketsMapper.map(salesPeriod.getFootballMatchTickets()))
                .createdAt(salesPeriod.getCreatedAt())
                .startDate(salesPeriod.getStartDate())
                .endDate(salesPeriod.getEndDate())
                .build();
    }

    public static SalesPeriod map(SalesPeriodDAO dao) {
        return SalesPeriod.builder()
                .id(dao.getId())
                .footballMatchTickets(FootballMatchTicketsMapper.fromDAOSToDomains(dao.getFootballMatchTickets()))
                .createdAt(dao.getCreatedAt())
                .startDate(dao.getStartDate())
                .endDate(dao.getEndDate())
                .build();
    }
}
