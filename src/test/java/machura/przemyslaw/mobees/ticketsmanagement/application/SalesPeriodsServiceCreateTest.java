package machura.przemyslaw.mobees.ticketsmanagement.application;

import io.vavr.control.Either;
import machura.przemyslaw.mobees.ticketsmanagement.Utils;
import machura.przemyslaw.mobees.ticketsmanagement.api.salesperiods.CreateQuarterlyRequest;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.SalesPeriodsPersistenceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesPeriodsServiceCreateTest {

    @Mock
    private SalesPeriodsPersistenceService persistenceService;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private SalesPeriodsService salesPeriodsService;

    @Test
    public void shouldNotCreateSalesPeriodIfItAlreadyExists(){
        SalesPeriod salesPeriod = Mockito.mock(SalesPeriod.class);
        when(persistenceService.findByQuarterRange(any(LocalDate.class))).thenReturn(Either.right(Collections.singletonList(salesPeriod)));

        Either<Failure, SalesPeriod> salesPeriodMaybe = salesPeriodsService.create(new CreateQuarterlyRequest(150));

        assertTrue(salesPeriodMaybe.isLeft());
        verify(persistenceService, never()).createAll(salesPeriod);
    }

    @Test
    public void shouldReturnFailureInCaseOfPersistenceServiceProblem(){
        when(timeProvider.now()).thenReturn(LocalDate.of(2020, 8, 13));
        when(persistenceService.findByQuarterRange(any(LocalDate.class))).thenReturn(Either.right(Collections.emptyList()));
        when(persistenceService.createAll(any(SalesPeriod.class))).thenReturn(Either.left(Failure.from("Persistence service error")));

        Either<Failure, SalesPeriod> salesPeriodMaybe = salesPeriodsService.create(new CreateQuarterlyRequest(150));

        assertTrue(salesPeriodMaybe.isLeft());
    }

    @ParameterizedTest
    @MethodSource("salesPeriodsCreationTestCases")
    public void shouldCreateQuarterlySalesPeriod(Utils.SalesPeriodsTestCase testCase) {
        when(timeProvider.now()).thenReturn(testCase.createSalesPeriodRequestDate);
        when(persistenceService.createAll(any(SalesPeriod.class))).thenAnswer(i -> Either.right(i.getArgument(0)));
        when(persistenceService.findByQuarterRange(any(LocalDate.class))).thenReturn(Either.right(Collections.emptyList()));

        Either<Failure, SalesPeriod> salesPeriodMaybe = salesPeriodsService.create(new CreateQuarterlyRequest(testCase.requestReducedTicketPool));

        assertTrue(salesPeriodMaybe.isRight());
        SalesPeriod salesPeriod = salesPeriodMaybe.get();
        Assertions.assertEquals(13, salesPeriod.getFootballMatchTickets().size());
        Assertions.assertEquals(testCase.matchesWithOpenSale, salesPeriod.matchesWithOpenSale(timeProvider));
        Assertions.assertEquals(testCase.matchesWithClosedSale, salesPeriod.matchesWithClosedSale(timeProvider));
        Assertions.assertEquals(testCase.matchesWhereTotalTicketsPoolIsEmpty, salesPeriod.matchesWhereTotalTicketsPoolIsEmpty());
        Assertions.assertEquals(testCase.reducedTicketsPool, salesPeriod.reducedTicketsPool());
        Assertions.assertEquals(testCase.totalTicketsPool, salesPeriod.totalTicketsPool());
        Utils.testReducedTickets(testCase.reducedTickets, salesPeriod);
    }

    private static List<Utils.SalesPeriodsTestCase> salesPeriodsCreationTestCases() {
        return List.of(

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .requestReducedTicketPool(300)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(7)
                        .matchesWithClosedSale(6)
                        .matchesWhereTotalTicketsPoolIsEmpty(6)
                        .reducedTicketsPool(300)
                        .totalTicketsPool(350)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 43, 43, 43, 43, 43, 43, 42))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .requestReducedTicketPool(1000)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(7)
                        .matchesWithClosedSale(6)
                        .matchesWhereTotalTicketsPoolIsEmpty(6)
                        .reducedTicketsPool(350)
                        .totalTicketsPool(350)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .requestReducedTicketPool(-500)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(7)
                        .matchesWithClosedSale(6)
                        .matchesWhereTotalTicketsPoolIsEmpty(6)
                        .reducedTicketsPool(0)
                        .totalTicketsPool(350)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 8, 13))
                        .requestReducedTicketPool(-500)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(7)
                        .matchesWithClosedSale(6)
                        .matchesWhereTotalTicketsPoolIsEmpty(6)
                        .reducedTicketsPool(0)
                        .totalTicketsPool(350)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 9, 30))
                        .requestReducedTicketPool(300)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(0)
                        .matchesWithClosedSale(13)
                        .matchesWhereTotalTicketsPoolIsEmpty(13)
                        .reducedTicketsPool(0)
                        .totalTicketsPool(0)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 7, 7))
                        .requestReducedTicketPool(300)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(12)
                        .matchesWithClosedSale(1)
                        .matchesWhereTotalTicketsPoolIsEmpty(1)
                        .reducedTicketsPool(300)
                        .totalTicketsPool(600)
                        .reducedTickets(Arrays.asList(0, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25))
                        .build(),

                Utils.SalesPeriodsTestCase.builder()
                        .createSalesPeriodRequestDate(LocalDate.of(2020, 9, 22))
                        .requestReducedTicketPool(300)
                        .footballMatchTicketsSize(13)
                        .matchesWithOpenSale(1)
                        .matchesWithClosedSale(12)
                        .matchesWhereTotalTicketsPoolIsEmpty(12)
                        .reducedTicketsPool(50)
                        .totalTicketsPool(50)
                        .reducedTickets(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50))
                        .build()
        );
    }


}
