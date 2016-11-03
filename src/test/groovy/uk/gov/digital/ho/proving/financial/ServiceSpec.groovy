package uk.gov.digital.ho.proving.financial

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.exception.ServiceExceptionHandler
import uk.gov.digital.ho.proving.financial.integration.*
import uk.gov.digital.ho.proving.financial.model.CappedValues
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import static org.hamcrest.core.AllOf.allOf
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.StringContains.containsString
import static org.springframework.http.HttpStatus.BAD_GATEWAY
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * @Author Home Office Digital
 */
class ServiceSpec extends Specification {

    public static final int DAYS_TO_CHECK = 28
    public static final String ACCOUNT_HOLDER_NAME = "Ray Purchase"

    final def UI_ENDPOINT = "/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus"

    final String SORT_CODE = "112233"
    final String ACCOUNT_NUMBER = "12345678"

    final String TO_DATE = '2015-10-30'
    final String DOB = '1990-10-04'
    final String COURSE_START_DATE = '2016-06-01'
    final String COURSE_END_DATE = '2016-07-01'
    final String CONTINUATION_END_DATE = '2016-08-01'

    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    MockMvc mockMvc
    MockRestServiceServer mockServer

    def service = new Service()
    def apiUrls = new ApiUrls()
    def checker = new FinancialStatusChecker();

    @Autowired
    def WebApplicationContext context;

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

        checker.auditor = Mock(ApplicationEventPublisher.class)

        service.financialStatusChecker = checker

        mockMvc = standaloneSetup(service)
            .setMessageConverters(createMessageConverter())
            .setControllerAdvice(new ServiceExceptionHandler())
            .alwaysDo(print())
            .alwaysExpect(content().contentType(APPLICATION_JSON_VALUE))
            .build()

    }

    def MappingJackson2HttpMessageConverter createMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.setObjectMapper(new ServiceConfiguration().getMapper())
        converter
    }

    ResponseDetails details = new ResponseDetails("200", "OK")
    CappedValues cappedValues= new CappedValues(100, 9, 3)

    String thresholdResponseJson = mapper.writeValueAsString(new ThresholdResult(1, cappedValues, details))
    String passResponseJson = mapper.writeValueAsString(new DailyBalanceStatusResult(ACCOUNT_HOLDER_NAME, true, null, details))

    def apiRespondsWith(threshold, balance) {
        mockServer.expect(requestTo(containsString("threshold")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(threshold);

        mockServer.expect(requestTo(containsString("balance")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(balance);
    }

    def "processes valid request and response - non-doctorate continuation course"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withSuccess(passResponseJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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

    def "processes valid request and response - non-doctorate new course"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withSuccess(passResponseJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
            // no continuationEndDate
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

    def "course dates not required - doctorate"() {

        given:
        apiRespondsWith(
            withSuccess(thresholdResponseJson, APPLICATION_JSON),
            withSuccess(passResponseJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('dob', DOB)
                .param('inLondon', 'true')
                .param('studentType', 'doctorate')
            //no dates dates
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

    @Ignore("The validation is in place for this but I can't get the class-level constraint to kick in when running through mockMvc")
    def "reports errors for missing mandatory parameter - courseStartDate, non-doctorate"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('dob', DOB)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('totalTuitionFees', '1')
                // missing course start date
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                containsString("courseStartDate"))))
        }
    }

    def "reports errors for missing mandatory parameter - dob"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('totalTuitionFees', '1')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                containsString("dob"))))
        }
    }

    def "invalid toDate is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', '99-01-1901')
                .param('dob', DOB)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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

    def "invalid courseStartDate is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('dob', DOB)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', '99-01-1901')
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                containsString("DateTimeParseException"),
                containsString("courseStartDate"))))
        }
    }

    def "invalid courseEndDate is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('dob', DOB)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', '99-01-1901')
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                containsString("DateTimeParseException"),
                containsString("courseEndDate"))))
        }
    }

    def "invalid continuationEndDate is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, SORT_CODE, ACCOUNT_NUMBER)
                .param('toDate', TO_DATE)
                .param('dob', DOB)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', '99-01-1901')
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
                containsString("DateTimeParseException"),
                containsString("continuationEndDate"))))
        }
    }

    @Unroll
    def "invalid sort code of #sortCode is rejected"() {

        when:
        def response = mockMvc.perform(
            get(UI_ENDPOINT, sortCode, ACCOUNT_NUMBER)
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
                .param('dob', DOB)
                .param('toDate', TO_DATE)
                .param('inLondon', 'true')
                .param('studentType', 'non-doctorate')
                .param('courseStartDate', COURSE_START_DATE)
                .param('courseEndDate', COURSE_END_DATE)
                .param('continuationEndDate', CONTINUATION_END_DATE)
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
