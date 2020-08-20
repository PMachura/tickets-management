package machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Optional;


@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class CreateQuarterlyRequest {
    @Min(0)
    final int reducedTicketsPool;
    final LocalDate salesPeriodDate;

    public Optional<LocalDate> getSalesPeriodDate(){
        return Optional.ofNullable(salesPeriodDate);
    }

}
