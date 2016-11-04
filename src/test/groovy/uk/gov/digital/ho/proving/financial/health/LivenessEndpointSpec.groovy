package uk.gov.digital.ho.proving.financial.health

import spock.lang.Specification

/**
 * Created by Home Office Digital.
 */
class LivenessEndpointSpec extends Specification {

    def "should answer with pong"() {

        expect:
        new LivenessEndpoint().invoke().equalsIgnoreCase("pong")
    }

    def "should be called ping" () {

        expect:
        new LivenessEndpoint().getId().equalsIgnoreCase("ping")
    }
}
