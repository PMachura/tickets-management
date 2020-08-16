package machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SalesPeriodSnapshot {
    private final List<Tuple2<LocalDate, Integer>> matchDatesWithReducedTickets;

    public static SalesPeriodSnapshot from(SalesPeriod salesPeriod) {
        return new SalesPeriodSnapshot(
                salesPeriod.getFootballMatchTickets().stream()
                        .map(footballMatchTickets -> Tuple.of(
                                footballMatchTickets.getMatchDate(),
                                footballMatchTickets.getReducedTicketsPool()))
                        .collect(Collectors.toList())
        );
    }
}
