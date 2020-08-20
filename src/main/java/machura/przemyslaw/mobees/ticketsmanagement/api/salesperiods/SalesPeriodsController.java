package machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods;

import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.application.SalesPeriodsService;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

@Controller
@RequestMapping(path = "sales-periods",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SalesPeriodsController {

    private static final String QUARTERLY_PATH = "quarterly";
    private static final String QUARTERLY_GET_PATH = "quarterly/{date}";
    private static final String QUARTERLY_DELETE_PATH = "quarterly/{date}";
    private static final String QUARTERLY_ADJUST_REDUCED_TICKETS_PATH = QUARTERLY_PATH + "/adjust-reduced-tickets";

    private static final Function<Failure, ResponseEntity> illegalInputHandler = failure -> ResponseEntity.badRequest().body(failure.getReason());
    private static final Function<Failure, ResponseEntity> resourceNotFoundHandler = failure -> ResponseEntity.notFound().build();
    private static final Function<Failure, ResponseEntity> improperRequestHandler = failure -> ResponseEntity.badRequest().body(failure.getReason());
    private static final Function<Failure, ResponseEntity> defaultRFailureHandler = failure -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");

    private static final Map<Failure.Status, Function<Failure, ResponseEntity>> failureHandlers = Map.of(
            Failure.Status.ILLEGAL_INPUT, illegalInputHandler,
            Failure.Status.RESOURCE_NOT_FOUND, resourceNotFoundHandler,
            Failure.Status.IMPROPER_REQUEST, improperRequestHandler,
            Failure.Status.SERVER_ERROR, defaultRFailureHandler
    );

    private final SalesPeriodsService salesPeriodsService;

    @PostMapping(path = QUARTERLY_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createQuarterlySalesPeriod(@RequestBody @Valid CreateQuarterlyRequest createQuarterlyRequest) {
        return salesPeriodsService.create(createQuarterlyRequest)
                .fold(SalesPeriodsController::getResponseFromFailure,
                        salesPeriod -> ResponseEntity.status(HttpStatus.CREATED).body(SalesPeriodSnapshot.from(salesPeriod)));
    }

    @GetMapping(path = QUARTERLY_GET_PATH)
    public ResponseEntity getQuarterlySalesPeriod(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return salesPeriodsService.findQuarterlySalesPeriod(date)
                .fold(SalesPeriodsController::getResponseFromFailure,
                        salesPeriod -> ResponseEntity.status(HttpStatus.OK).body(SalesPeriodSnapshot.from(salesPeriod)));

    }

    @DeleteMapping(path = QUARTERLY_DELETE_PATH)
    public ResponseEntity deleteQuarterlySalesPeriod(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return salesPeriodsService.deleteQuarterlySalesPeriod(date)
                .fold(SalesPeriodsController::getResponseFromFailure,
                        success -> ResponseEntity.status(HttpStatus.OK).body(success.getMessage()));

    }

    @PatchMapping(path = QUARTERLY_ADJUST_REDUCED_TICKETS_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity quarterlySalesPeriodAdjustReducedTickets(@RequestBody AdjustReducedTicketsRequest adjustReducedTicketsRequest) {
        return salesPeriodsService.adjustTicketsForQuarterlySalesPeriod(adjustReducedTicketsRequest)
                .fold(SalesPeriodsController::getResponseFromFailure,
                        salesPeriod -> ResponseEntity.status(HttpStatus.OK).body(SalesPeriodSnapshot.from(salesPeriod)));
    }

    private static ResponseEntity getResponseFromFailure(Failure failure) {
        return failureHandlers.getOrDefault(failure.getStatus(), defaultRFailureHandler)
                .apply(failure);
    }

}
