package machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods;

import java.time.DayOfWeek;
import java.time.LocalDate;

public interface SalesPeriodSpec {
    LocalDate requestDate();
    LocalDate startDate();
    LocalDate endDate();
    DayOfWeek matchDay();
    int reducedTicketsPool();
    int singleMatchTotalTicketsPool();
}
