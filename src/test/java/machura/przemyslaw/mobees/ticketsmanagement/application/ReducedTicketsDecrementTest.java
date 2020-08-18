package machura.przemyslaw.mobees.ticketsmanagement.application;

import io.vavr.control.Either;
import machura.przemyslaw.mobees.ticketsmanagement.Utils;
import machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods.CreateQuarterlyRequest;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.common.Success;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.SalesPeriodsPersistenceService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReducedTicketsDecrementTest {

    @Mock
    private SalesPeriodsPersistenceService persistenceService;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private SalesPeriodsService salesPeriodsService;

    // todo improve
    @ParameterizedTest
    @MethodSource("salesPeriodsReducedTicketIncrementTestCases")
    public void shouldCreateQuarterlySalesPeriod(Utils.SalesPeriodsTestCase testCase) {
        when(timeProvider.now()).thenReturn(testCase.createSalesPeriodRequestDate);
        when(persistenceService.createAll(any(SalesPeriod.class))).thenAnswer(i -> Either.right(i.getArgument(0)));
        when(persistenceService.findByQuarterRange(any(LocalDate.class))).thenReturn(Either.right(Collections.emptyList()));

        Either<Failure, SalesPeriod> salesPeriodMaybe = salesPeriodsService.create(new CreateQuarterlyRequest(testCase.requestReducedTicketPool));
        SalesPeriod salesPeriod = salesPeriodMaybe.get();

        when(timeProvider.now()).thenReturn(testCase.adjustReducedTicketsRequestDate);
        ReducedTicketsDecrement ticketsAdjuster = ReducedTicketsDecrement.create(timeProvider, testCase.requestReducedTicketAdjustment).get();
        Either<Failure, Success> adjustmentResult = salesPeriod.adjustTickets(ticketsAdjuster, timeProvider);

        assertTrue(adjustmentResult.isRight());
        Utils.testReducedTickets(testCase.adjustedReducedTickets, salesPeriod);
    }

    private static List<Utils.SalesPeriodsTestCase> salesPeriodsReducedTicketIncrementTestCases() {
        return List.of(

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .adjustReducedTicketsRequestDate(LocalDate.of(2020, 8, 13))
                        .requestReducedTicketPool(300)
                        .requestReducedTicketAdjustment(-7)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 42))
                        .adjustedReducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 42, 42, 42, 42, 42, 42, 41))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .adjustReducedTicketsRequestDate(LocalDate.of(2020, 8, 18))
                        .requestReducedTicketPool(300)
                        .requestReducedTicketAdjustment(-7)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 42))
                        .adjustedReducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 43, 41, 42, 42, 42, 42, 41))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .adjustReducedTicketsRequestDate(LocalDate.of(2020, 8, 13))
                        .requestReducedTicketPool(300)
                        .requestReducedTicketAdjustment(-500)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 42))
                        .adjustedReducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .build()
        );
    }
}
