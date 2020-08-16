package machura.przemyslaw.mobees.ticketsmanagement.application;

import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.common.Utils;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriodSpec;

import java.time.DayOfWeek;
import java.time.LocalDate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
class SalesPeriodSpecQuarterly implements SalesPeriodSpec {

    private final LocalDate requestDate;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final DayOfWeek matchDay;
    private final int reducedTicketPool;
    private final int singleMatchTotalTicketPool;

    public static SalesPeriodSpecQuarterly from(LocalDate requestDate,
                                                DayOfWeek matchDay,
                                                int singleMatchTotalTicketsPool,
                                                int reducedTicketPool) {
        Tuple2<LocalDate, LocalDate> firstAndLastDayOfQuarter = Utils.getFirstAndLastDayOfQuarter(requestDate);
        LocalDate firstDayOfQuarter = firstAndLastDayOfQuarter._1;
        LocalDate lastDayOfQuarter = firstAndLastDayOfQuarter._2;
        return SalesPeriodSpecQuarterly
                .builder()
                .singleMatchTotalTicketPool(Math.max(singleMatchTotalTicketsPool, 0))
                .reducedTicketPool(Math.max(reducedTicketPool, 0))
                .requestDate(requestDate)
                .startDate(firstDayOfQuarter)
                .endDate(lastDayOfQuarter)
                .matchDay(matchDay)
                .build();
    }

    @Override
    public LocalDate requestDate() {
        return requestDate;
    }

    @Override
    public LocalDate startDate() {
        return startDate;
    }

    @Override
    public LocalDate endDate() {
        return endDate;
    }

    @Override
    public DayOfWeek matchDay() {
        return matchDay;
    }

    @Override
    public int reducedTicketsPool() {
        return reducedTicketPool;
    }

    @Override
    public int singleMatchTotalTicketsPool() {
        return singleMatchTotalTicketPool;
    }
}
