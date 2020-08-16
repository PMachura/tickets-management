package machura.przemyslaw.mobees.ticketsmanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

    @AllArgsConstructor
    @Builder
    public static class SalesPeriodsTestCase {
        public final LocalDate createSalesPeriodRequestDate;
        public final LocalDate adjustReducedTicketsRequestDate;
        public final int requestReducedTicketPool;
        public final int requestReducedTicketAdjustment;

        public final int footballMatchTicketsSize;
        public final int matchesWithClosedSale;
        public final int matchesWithOpenSale;
        public final int matchesWhereTotalTicketsPoolIsEmpty;
        public final int reducedTicketsPool;
        public final int totalTicketsPool;
        
        @Singular
        public final List<Integer> reducedTickets;
        @Singular
        public final List<Integer> adjustedReducedTickets;
    }

    public static void testReducedTickets(List<Integer> expected, SalesPeriod salesPeriod) {
        List<Integer> reducedTicketsPools = salesPeriod.getFootballMatchTickets().stream()
                .map(FootballMatchTickets::getReducedTicketsPool)
                .collect(Collectors.toList());
        IntStream.range(0, expected.size())
                .forEach(i -> Assertions.assertEquals(expected.get(i), reducedTicketsPools.get(i)));
    }

}


