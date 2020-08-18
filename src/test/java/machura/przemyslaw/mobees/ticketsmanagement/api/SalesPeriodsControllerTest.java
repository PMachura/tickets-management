package machura.przemyslaw.mobees.ticketsmanagement.api;

import io.vavr.control.Either;
import machura.przemyslaw.mobees.ticketsmanagement.application.SalesPeriodsService;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class SalesPeriodsControllerTest {

    private static final String QUARTERLY_PATH = "/sales-periods/quarterly";
    private static final String QUARTERLY_ADJUST_REDUCED_TICKETS_PATH = QUARTERLY_PATH + "/adjust-reduced-tickets";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalesPeriodsService salesPeriodsService;

    @Test
    public void shouldReturnNotFoundWhenSalesPeriodsIsNotFound() throws Exception {
        when(salesPeriodsService.findQuarterlySalesPeriod(any())).thenReturn(Either.left(Failure.from("Not found", Failure.Status.RESOURCE_NOT_FOUND)));
        this.mockMvc.perform(get(QUARTERLY_PATH + "/2020-10-10")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnOkWhenSalesPeriodIsFound() throws Exception {
        SalesPeriod salesPeriod = Mockito.mock(SalesPeriod.class);
        when(salesPeriodsService.findQuarterlySalesPeriod(any())).thenReturn(Either.right(salesPeriod));
        this.mockMvc.perform(get(QUARTERLY_PATH + "/2020-10-10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
