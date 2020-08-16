package machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods;

import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.application.SalesPeriodsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping(path = "sales-periods",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SalesPeriodsController {

    private static final String QUARTERLY_PATH = "quarterly";
    private static final String QUARTERLY_GET_PATH = "quarterly/{date}";
    private static final String QUARTERLY_ADJUST_REDUCED_TICKETS_PATH = QUARTERLY_PATH + "/adjust-reduced-tickets";

    private final SalesPeriodsService salesPeriodsService;

    @PostMapping(path = QUARTERLY_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createQuarterlySalesPeriod(@RequestBody @Valid CreateQuarterlyRequest createQuarterlyRequest) {
        return salesPeriodsService.create(createQuarterlyRequest)
                .fold(failure -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failure.getReason()),
                        salesPeriod -> ResponseEntity.status(HttpStatus.CREATED).body(SalesPeriodSnapshot.from(salesPeriod)));
    }

    @GetMapping(path = QUARTERLY_GET_PATH)
    public ResponseEntity getQuarterlySalesPeriod(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return salesPeriodsService.findQuarterlySalesPeriod(date)
                .fold(failure -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failure.getReason()),
                        salesPeriodMaybe -> salesPeriodMaybe.map(salesPeriod -> ResponseEntity.status(HttpStatus.OK).body(SalesPeriodSnapshot.from(salesPeriod)))
                                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()));

    }

    @PatchMapping(path = QUARTERLY_ADJUST_REDUCED_TICKETS_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity quarterlySalesPeriodAdjustReducedTickets(@RequestBody AdjustReducedTicketsRequest adjustReducedTicketsRequest) {
        return salesPeriodsService.adjustTicketsForQuarterlySalesPeriod(adjustReducedTicketsRequest)
                .fold(failure -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failure.getReason()),
                        salesPeriod -> ResponseEntity.status(HttpStatus.OK).body(SalesPeriodSnapshot.from(salesPeriod)));
    }

}
