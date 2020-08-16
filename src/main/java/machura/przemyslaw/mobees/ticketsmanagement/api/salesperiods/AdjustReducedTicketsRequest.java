package machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(force = true)
@Getter
public class AdjustReducedTicketsRequest {
    final int reducedTicketsChange;
    final LocalDate salesPeriodDate;
}
