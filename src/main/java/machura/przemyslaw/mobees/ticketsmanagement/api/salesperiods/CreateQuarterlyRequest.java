package machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods;

import lombok.*;

import javax.validation.constraints.Min;


@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class CreateQuarterlyRequest {
    @Min(0)
    final int reducedTicketsPool;
}
