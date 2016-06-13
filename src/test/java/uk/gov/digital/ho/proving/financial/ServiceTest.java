package uk.gov.digital.ho.proving.financial;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.DailyBalanceCheck;
import uk.gov.digital.ho.proving.financial.model.DailyBalanceCheckResponse;
import uk.gov.digital.ho.proving.financial.model.ResponseStatus;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @Author Home Office Digital
 */
public class ServiceTest {

    public static final String UI_ENDPOINT = "/incomeproving/v1/individual/financialstatus";

    public static final String API_ENDPOINT = "/incomeproving/v1/individual/dailybalancecheck";

    private MockMvc mockMvc;

    @Mock
    private CounterService counterService;

    @Mock
    private Client mockClient;

    @Mock
    private WebResource webResource;

    @Mock
    private WebResource.Builder mockBuilder;

    @Mock
    private ClientResponse clientResponse;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        Service service = new Service();

        ReflectionTestUtils.setField(service, "counterService", counterService);
        ReflectionTestUtils.setField(service, "client", mockClient);

        ReflectionTestUtils.setField(service, "apiRoot", "");
        ReflectionTestUtils.setField(service, "apiEndpoint", API_ENDPOINT);
        ReflectionTestUtils.setField(service, "daysToCheck", "27");

        mockMvc = MockMvcBuilders.standaloneSetup(service).setMessageConverters(createMessageConverter())
            .alwaysDo(print())
            .alwaysExpect(content().contentType(APPLICATION_JSON_VALUE))
            .build();
    }

    public MappingJackson2HttpMessageConverter createMessageConverter() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ServiceConfiguration().getMapper());

        return converter;
    }


    @Test
    public void shouldProcessValidRequestResponse() throws Exception {

        String ui_url = UI_ENDPOINT + "/funds?accountNumber=12345678&sortCode=20-01-01&totalFundsRequired=1&maintenancePeriodEndDate=2015-10-30";

        String api_url = API_ENDPOINT + "?accountNumber=12345678&sortCode=20-01-01&threshold=1&applicationRaisedDate=2015-10-30&days=27";

        URI uri = UriComponentsBuilder.fromUriString(api_url).build().toUri();

        DailyBalanceCheckResponse result = new DailyBalanceCheckResponse(
            anAccount("11-22-33", "12345678"),
            aDailyBalanceCheck(LocalDate.of(2015, 01, 30), 1, true),
            aResponseStatus("200", "OK"));

        withResponse(uri, Response.Status.OK);
        withApiResult(result);

        mockMvc
            .perform(get(ui_url))
            .andExpect(status().isOk())
            .andExpect(jsonPath("fundingRequirementMet", is(true)));
    }

    @Test
    public void shouldReportErrorForMissingParameter() throws Exception {

        String ui_url = UI_ENDPOINT + "/funds?&sortCode=01-01-01&totalFundsRequired=0&maintenancePeriodEndDate=2016-10-10";

        this.mockMvc
            .perform(get(ui_url))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("code", is("0008")))
            .andExpect(jsonPath("message", allOf(containsString("Missing parameter"), containsString("accountNumber"))));
    }

    private void withResponse(URI url, Response.Status status) {
        when(mockClient.resource(url)).thenReturn(webResource);
        when(webResource.header("accept", "application/json")).thenReturn(mockBuilder);
        when(mockBuilder.header("content-type", "application/json")).thenReturn(mockBuilder);
        when(mockBuilder.get(ClientResponse.class)).thenReturn(clientResponse);
        when(clientResponse.getStatusInfo()).thenReturn(status);
        when(clientResponse.getStatus()).thenReturn(status.getStatusCode());
    }

    private void withApiResult(DailyBalanceCheckResponse result) {
        when(clientResponse.getEntity(DailyBalanceCheckResponse.class)).thenReturn(result);
    }

    private Account anAccount(String sortCode, String accountNumber) {
        return new Account(sortCode, accountNumber);
    }

    private DailyBalanceCheck aDailyBalanceCheck(LocalDate aDate, int threshold, boolean minimumAboveThreshold) {
        return new DailyBalanceCheck(aDate, aDate.minusDays(27), BigDecimal.valueOf(threshold), minimumAboveThreshold);
    }

    private ResponseStatus aResponseStatus(String code, String message) {
        return new ResponseStatus(code, message);
    }
}
