package uk.gov.digital.ho.proving.financial;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @Author Home Office Digital
 */
public class ServiceTest {

    private MockMvc mockMvc;

    @Mock
    private CounterService counterService;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        Service service = new Service();
        ReflectionTestUtils.setField(service, "counterService", counterService);

        mockMvc = MockMvcBuilders.standaloneSetup(service)
            .alwaysDo(print())
            .alwaysExpect(content().contentType(APPLICATION_JSON_VALUE))
            .build();
    }

    // todo - this is obviously just a dummy
    @Test
    public void shouldReportMeetingFinancialStatus() throws Exception {
        mockMvc
            .perform(get("/financialstatus/v1/status?accountNumber=11111111&sortCode=01-01-01&totalFundsRequired=0&maintenancePeriodEndDate=2016-10-10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("meetsFinancialStatusRequirements", is(true)));
    }

    @Test
    public void shouldReportErrorForMissingParameter() throws Exception {
        this.mockMvc
            .perform(get("/financialstatus/v1/status?sortCode=01-01-01&totalFundsRequired=0&maintenancePeriodEndDate=2016-10-10"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("code", is("0008")))
            .andExpect(jsonPath("message", allOf(containsString("Missing parameter"), containsString("accountNumber"))));
    }
}
