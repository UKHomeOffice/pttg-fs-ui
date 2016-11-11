package uk.gov.digital.ho.proving.financial.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Timeout
import uk.gov.digital.ho.proving.financial.health.indicator.ApiConnectionHealth

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Created by Home Office Digital.
 */
class ApiAvailabilityCheckerSpec extends Specification {

    def "should report DOWN when server unreachable"() {

        given:
        ApiAvailabilityChecker checker = new ApiAvailabilityChecker()
        checker.apiRoot = ''
        checker.apiEndpoint = ''

        when:
        ResponseEntity result = checker.check()

        then:
        result.statusCode == HttpStatus.SERVICE_UNAVAILABLE
    }

    def "should report UP when server is reachable"() {

        given:
        def connectionTester = Mock(UrlConnectionTester)

        ApiAvailabilityChecker checker = new ApiAvailabilityChecker()
        checker.tester = connectionTester

        and:
        connectionTester.getResponseCodeFor(*_) >> 200

        when:
        ResponseEntity result = checker.check()

        then:
        result.statusCode == HttpStatus.OK
    }

    @Timeout(value = 6, unit = SECONDS) // timeout big enough to allow for execution on slow laptops
    def "should report DOWN when server doesn't respond within timeout"() {

        given:
        ApiAvailabilityChecker checker = new ApiAvailabilityChecker()
        checker.apiRoot = 'http://10.255.255.1' // unresolvable address to cause timeout
        checker.apiEndpoint = ''

        checker.setTimeout(100) // short timeout so that the test doesn't take forever

        when:
        ResponseEntity result = checker.check()

        then:
        result.statusCode == HttpStatus.SERVICE_UNAVAILABLE
    }
}
