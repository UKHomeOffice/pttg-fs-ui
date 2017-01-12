package uk.gov.digital.ho.proving.financial

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification
import spock.lang.Timeout
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import static java.util.concurrent.TimeUnit.SECONDS
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @Author Home Office Digital
 */
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [ServiceRunner.class],
    properties = [
        "api.root=http://10.255.255.1",
        "rest.connection.connect-timeout=500",
        "connectionRetryDelay=500",
        "connectionAttemptCount=2"
    ])
class ServiceConnTimeoutIntegrationSpec extends Specification {

    def path = "/pttg/financialstatus/v1/t4/accounts/123456/12345678/dailybalancestatus?"
    def params = "dob=1990-10-04&toDate=2015-01-01&inLondon=true&studentType=nondoctorate&courseStartDate=2016-01-01&courseEndDate=2016-01-01&continuationEndDate&totalTuitionFees=1&tuitionFeesAlreadyPaid=1&accommodationFeesAlreadyPaid=1&numberOfDependants=1"
    def url

    @Autowired
    TestRestTemplate restTemplate

    def setup() {
        url = path + params
    }

    @Timeout(value = 4, unit = SECONDS)
    def 'obeys timeout on slow connection response'() {

        given:
        // we have set the api host to an unresolvable address, which means that the rest call to the API won't return
        // we have also set the connect-timeout property to 1/2 second

        when:
        def entity = restTemplate.getForEntity(url, ResponseDetails.class)

        then:
        entity.getBody().message.contains("connect timed out")
        // If the connection timeout settings aren't being obeyed, then this test will fail when the @Timeout is exceeded
    }


}
