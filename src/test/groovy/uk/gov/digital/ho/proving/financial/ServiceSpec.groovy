package uk.gov.digital.ho.proving.financial

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.exception.ApiExceptionHandler
import uk.gov.digital.ho.proving.financial.model.Account
import uk.gov.digital.ho.proving.financial.model.DailyBalanceStatusResponse
import uk.gov.digital.ho.proving.financial.model.ResponseDetails
import uk.gov.digital.ho.proving.financial.model.ThresholdResponse

import javax.ws.rs.core.Response
import java.time.LocalDate

import static org.hamcrest.core.AllOf.allOf
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.StringContains.containsString
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
class ServiceSpec extends Specification {

    public static final int DAYS_TO_CHECK = 28

    final def UI_ENDPOINT = "/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus"

    final String SORT_CODE = "112233"
    final String ACCOUNT_NUMBER = "12345678"

    final String TO_DATE = '2015-10-30'
    final String FROM_DATE = '2015-10-03'

    final BigDecimal FUNDS_REQUIRED = 100.0

    MockMvc mockMvc

    def mockClient = Mock(Client)
    def balanceResource = Mock(WebResource)
    def thresholdResource = Mock(WebResource)
    def balanceBuilder = Mock(WebResource.Builder)
    def thresholdBuilder = Mock(WebResource.Builder)
    def balanceResponse = Mock(ClientResponse)
    def thresholdResponse = Mock(ClientResponse)

    def service = new Service()
    def apiUrls = new ApiUrls()

    def setup() {

        service.client = mockClient

        apiUrls.apiRoot = ''
        apiUrls.apiDailyBalanceEndpoint = "/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus"
        apiUrls.apiThresholdEndpoint = "/pttg/financialstatusservice/v1/maintenance/threshold"

        service.daysToCheck = DAYS_TO_CHECK
        service.apiUrls = apiUrls

        mockMvc = standaloneSetup(service)
            .setMessageConverters(createMessageConverter())
            .setControllerAdvice(new ApiExceptionHandler())
            .alwaysDo(print())
            .alwaysExpect(content().contentType(APPLICATION_JSON_VALUE))
            .build()
    }

    def MappingJackson2HttpMessageConverter createMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.setObjectMapper(new ServiceConfiguration().getMapper())
        converter
    }

    DailyBalanceStatusResponse passResponse = new DailyBalanceStatusResponse(
        new Account(SORT_CODE, ACCOUNT_NUMBER),
        LocalDate.parse(FROM_DATE),
        LocalDate.parse(TO_DATE),
        BigDecimal.valueOf(FUNDS_REQUIRED),
        true,
        new ResponseDetails("200", "OK"))

    DailyBalanceStatusResponse notFoundResponse = new DailyBalanceStatusResponse(
        null,
        null,
        null,
        null,
        false,
        new ResponseDetails("404", "Not Found"))


    def remoteApiDailyBalanceResponse(Response.Status status, DailyBalanceStatusResponse response) {

        mockClient.resource({ URI url -> url.getPath().contains("balance") }) >> balanceResource

        balanceResource.header("accept", "application/json") >> balanceBuilder
        balanceBuilder.header("content-type", "application/json") >> balanceBuilder

        balanceBuilder.get(ClientResponse.class) >> balanceResponse

        balanceResponse.getStatusInfo() >> status
        balanceResponse.getStatus() >> status.getStatusCode()
        balanceResponse.getEntity(DailyBalanceStatusResponse.class) >> response
    }

    def remoteApiThresholdResponse(Response.Status status, ThresholdResponse response) {

        mockClient.resource({ URI url -> url.getPath().contains("threshold") }) >> thresholdResource

        thresholdResource.header("accept", "application/json") >> thresholdBuilder
        thresholdBuilder.header("content-type", "application/json") >> thresholdBuilder

        thresholdBuilder.get(ClientResponse.class) >> thresholdResponse

        thresholdResponse.getStatusInfo() >> status
        thresholdResponse.getStatus() >> status.getStatusCode()
        thresholdResponse.getEntity(ThresholdResponse.class) >> response
    }

    def "processes valid request and response"() {

        given:
        remoteApiThresholdResponse(Response.Status.OK, new ThresholdResponse(1))
        remoteApiDailyBalanceResponse(Response.Status.OK, passResponse)

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isOk())
            andExpect(jsonPath("fundingRequirementMet", is(true)))
        }
    }


    def "reports errors for missing mandatory parameters"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0001")))
            andExpect(jsonPath("message", allOf(
                containsString("Missing parameter"),
                containsString("courseLength"))))
        }
    }

    def "invalid to date is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', '99-01-1901')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0002")))
            andExpect(jsonPath("message", allOf(
                containsString("Invalid parameter type"),
                containsString("toDate"))))
        }
    }

    @Unroll
    def "invalid sort code of #sortCode is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, sortCode, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0003")))
            andExpect(jsonPath("message", allOf(
                containsString("Invalid parameter format"),
                containsString("sortCode"))))
        }

        where:
        sortCode << ["12345", "000000", "1234567"]
    }

    @Unroll
    def "invalid account number of #accountNumber is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, accountNumber)
                .param('toDate', TO_DATE)
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0003")))
            andExpect(jsonPath("message", allOf(
                containsString("Invalid parameter format"),
                containsString("accountNumber"))))
        }

        where:
        accountNumber << ["1234567", "123456789", "00000000"]
    }

    def "reports remote server error as internal error"() {

        given:
        remoteApiThresholdResponse(Response.Status.OK, new ThresholdResponse(1))
        remoteApiDailyBalanceResponse(Response.Status.INTERNAL_SERVER_ERROR, null)

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0005")))
        }
    }

    def "reports remote server response processing error as internal error"() {

        given:
        remoteApiThresholdResponse(Response.Status.OK, new ThresholdResponse(1))
        remoteApiDailyBalanceResponse(Response.Status.OK, null)

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0000")))
        }
    }

    def "propagates remote server not found response"() {

        given:
        remoteApiThresholdResponse(Response.Status.OK, new ThresholdResponse(1))
        remoteApiDailyBalanceResponse(Response.Status.NOT_FOUND, notFoundResponse)

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1'))

        then:
        response.with {
            andExpect(status().isNotFound())
        }
    }

}
