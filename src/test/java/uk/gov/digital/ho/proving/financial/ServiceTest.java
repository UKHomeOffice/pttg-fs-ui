package uk.gov.digital.ho.proving.financial;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    public static final String UI_ENDPOINT = "/pttg/financialstatusservice/v1/accounts/";

    public static final String API_ENDPOINT = "/pttg/financialstatusservice/v1/accounts/";

    private MockMvc mockMvc;

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

        ReflectionTestUtils.setField(service, "client", mockClient);

        ReflectionTestUtils.setField(service, "apiRoot", "");
        ReflectionTestUtils.setField(service, "apiEndpoint", API_ENDPOINT + "/{sortCode}/{accountNumber}/dailybalancestatus");
        ReflectionTestUtils.setField(service, "daysToCheck", 28);

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

        String ui_url = UI_ENDPOINT + "200101/12345678/dailybalancestatus?totalFundsRequired=1&toDate=2015-10-30";

        String api_url = API_ENDPOINT + "/200101/12345678/dailybalancestatus?minimum=1&fromDate=2015-10-03&toDate=2015-10-30";

        URI uri = UriComponentsBuilder.fromUriString(api_url).build().toUri();

        DailyBalanceCheckResponse result = new DailyBalanceCheckResponse(
            anAccount("112233", "12345678"),
            aDailyBalanceCheck(LocalDate.of(2015, 10, 30), 1, true),
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

        String ui_url = UI_ENDPOINT + "010101/12345678/dailybalancestatus?toDate=2016-10-10";

        this.mockMvc
            .perform(get(ui_url))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("code", is("0008")))
            .andExpect(jsonPath("message", allOf(containsString("Missing parameter"), containsString("totalFundsRequired"))));
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
