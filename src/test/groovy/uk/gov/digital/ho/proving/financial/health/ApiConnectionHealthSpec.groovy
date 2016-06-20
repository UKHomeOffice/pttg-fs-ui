package uk.gov.digital.ho.proving.financial.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status
import spock.lang.Specification

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
        connectionTester.getResponseCodeFor(_) >> 200

        when:
        Health result = healthCheck.health()

        then:
        result.getStatus() == Status.UP

        // todo tests for error status codes when we have a server healthcheck to call
    }
}
