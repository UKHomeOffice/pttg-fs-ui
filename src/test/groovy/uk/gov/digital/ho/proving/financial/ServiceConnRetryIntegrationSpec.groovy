package uk.gov.digital.ho.proving.financial

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification
import spock.lang.Timeout
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static java.util.concurrent.TimeUnit.SECONDS
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @Author Home Office Digital
 */
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [ServiceRunner.class],
    properties = [
        "api.root=http://localhost:8989",
        "rest.connection.connection-request-timeout=200",
        "rest.connection.connect-timeout=200",
        "rest.connection.read-timeout=200",
        "connectionRetryDelay=200",
        "connectionAttemptCount=3"
    ])
class ServiceConnRetryIntegrationSpec extends Specification {

    def path = "/pttg/financialstatus/v1/t4/accounts/123456/12345678/dailybalancestatus?"
    def params = "dob=1990-10-04&toDate=2015-01-01&inLondon=true&studentType=nondoctorate&courseStartDate=2016-01-01&courseEndDate=2016-01-01&originalCourseStartDate&totalTuitionFees=1&tuitionFeesAlreadyPaid=1&accommodationFeesAlreadyPaid=1&numberOfDependants=1"

    def url

    @Autowired
    TestRestTemplate restTemplate

    def apiServerMock
    def thresholdUrlRegex = "/pttg/financialstatus/v1/t4/maintenance/threshold*"

    def setup() {
        url = path + params

        apiServerMock = new WireMockTestDataLoader(8989)
    }

    def cleanup() {
        apiServerMock.stop()
    }

    @Timeout(value = 4, unit = SECONDS)
    // ensure it doesn't accidentally run forever...
    def 'retries API calls when Connection timeout'() {

        given:
        apiServerMock.withDelayedResponse(thresholdUrlRegex, 2)

        when:
        restTemplate.getForEntity(url, ResponseDetails.class)

        then:
        verify(3, getRequestedFor(urlPathMatching(thresholdUrlRegex)))
    }
}
