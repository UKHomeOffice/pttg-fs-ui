package uk.gov.digital.ho.proving.financial

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseCreator
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.exception.ServiceExceptionHandler
import uk.gov.digital.ho.proving.financial.integration.ApiUrls
import uk.gov.digital.ho.proving.financial.integration.DailyBalanceStatusResult
import uk.gov.digital.ho.proving.financial.integration.FinancialStatusChecker
import uk.gov.digital.ho.proving.financial.integration.RestServiceErrorHandler
import uk.gov.digital.ho.proving.financial.integration.ThresholdResult
import uk.gov.digital.ho.proving.financial.model.CappedValues
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import java.util.concurrent.TimeUnit

import static org.hamcrest.core.AllOf.allOf
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.StringContains.containsString
import static org.springframework.http.HttpStatus.BAD_GATEWAY
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.*
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

    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    MockMvc mockMvc
    MockRestServiceServer mockServer

    def service = new Service()
    def apiUrls = new ApiUrls()
    def checker = new FinancialStatusChecker();

    def setup() {

        apiUrls.apiRoot = ''
        apiUrls.apiDailyBalanceEndpoint = "/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus"
        apiUrls.apiThresholdEndpoint = "/pttg/financialstatusservice/v1/maintenance/threshold"

        RestTemplate restTemplate = new RestTemplate()
        restTemplate.errorHandler = new RestServiceErrorHandler();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        checker.restTemplate = restTemplate
        checker.daysToCheck = DAYS_TO_CHECK
        checker.apiUrls = apiUrls

        service.financialStatusChecker = checker

        mockMvc = standaloneSetup(service)
            .setMessageConverters(createMessageConverter())
            .setControllerAdvice(new ServiceExceptionHandler())
            .alwaysDo(print())
//            .alwaysExpect(content().contentType(APPLICATION_JSON_VALUE))
            .build()
    }

    def MappingJackson2HttpMessageConverter createMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.setObjectMapper(new ServiceConfiguration().getMapper())
        converter
    }

    ResponseDetails details = new ResponseDetails("200", "OK")
    CappedValues cappedValues= new CappedValues("100", 9)

    String thresholdResponseJson = mapper.writeValueAsString(new ThresholdResult(1, cappedValues, details))
    String passResponseJson = mapper.writeValueAsString(new DailyBalanceStatusResult(true, null, details))

    def apiRespondsWith(threshold, balance) {
        mockServer.expect(requestTo(containsString("threshold")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(threshold);

        mockServer.expect(requestTo(containsString("balance")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(balance);
    }

    def "processes valid request and response"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withSuccess(passResponseJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isOk())
            andExpect(content().contentType(APPLICATION_JSON_VALUE))
            andExpect(jsonPath("fundingRequirementMet", is(true)))
        }
    }


    def "reports errors for missing mandatory parameters"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

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
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0002")))
            andExpect(jsonPath("message", allOf(
                containsString("Invalid parameter"),
                containsString("toDate"))))
        }
    }

    @Unroll
    def "invalid sort code of #sortCode is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, sortCode, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

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
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

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


    def "reports remote server error for threshold as internal error"() {

        given:
        apiRespondsWith(
            withServerError(),
            withSuccess(thresholdResponseJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0006")))
        }
    }


    def "reports remote server error for dailybalancestatus as internal error"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withServerError()
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0006")))
        }
    }


    def "reports remote server response processing error as internal error"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withSuccess(null, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0005")))
        }
    }


    def "when 404 at API, returns 404 - the 'insufficient information' case"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withStatus(NOT_FOUND)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isNotFound())
        }
    }

    def 'handles unexpected HTTP status from API server'() {

        given:
        apiRespondsWith(
            withStatus(BAD_GATEWAY),
            withStatus(BAD_GATEWAY)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseLength', '1')
                .param('totalTuitionFees', '1')
                .param('tuitionFeesAlreadyPaid', '1')
                .param('accommodationFeesAlreadyPaid', '1')
                .param('numberOfDependants', '1')
        )

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0005")))
            andExpect(jsonPath("message", containsString("API response status")))
            andExpect(jsonPath("message", containsString(BAD_GATEWAY.toString())))
        }
    }


}
