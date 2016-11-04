package uk.gov.digital.ho.proving.financial.health

import org.junit.rules.Stopwatch
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status
import spock.lang.Specification
import spock.lang.Timeout

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * @Author Home Office Digital
 */
class ApiConnectionHealthSpec extends Specification {

    def "should report DOWN when server unreachable"() {

        given:
        ApiConnectionHealth healthCheck = new ApiConnectionHealth()
        healthCheck.apiRoot = ''
        healthCheck.apiEndpoint = ''

        when:
        Health result = healthCheck.health()

        then:
        result.getStatus() == Status.DOWN
    }

    def "should report UP when server is reachable"() {

        given:
        def connectionTester = Mock(ApiConnectionHealth.UrlConnectionTester)

        ApiConnectionHealth healthCheck = new ApiConnectionHealth()
        healthCheck.tester = connectionTester

        and:
        connectionTester.getResponseCodeFor(*_) >> 200

        when:
        Health result = healthCheck.health()

        then:
        result.getStatus() == Status.UP
    }

    @Timeout(value = 6, unit = SECONDS) // timeout big enough to allow for execution on slow laptops
    def "should report DOWN when server doesn't respond within timeout"() {

        given:
        ApiConnectionHealth healthCheck = new ApiConnectionHealth()
        healthCheck.apiRoot = 'http://10.255.255.1' // unresolvable address to cause timeout
        healthCheck.apiEndpoint = ''

        healthCheck.setTimeout(100) // short timeout so that the test doesn't take forever

        when:
        Health result = healthCheck.health()

        then:
        result.getStatus() == Status.DOWN
    }
}
