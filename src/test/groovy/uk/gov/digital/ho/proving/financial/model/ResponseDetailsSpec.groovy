package uk.gov.digital.ho.proving.financial.model

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

/**
 * @Author Home Office Digital
 */
class ResponseDetailsSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new ResponseDetails("200", "message")

        when:
        def output = instance.toString()

        then:
        output.contains("code='$instance.code'")

        and:
        !output.contains('ResponseDetails@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(ResponseDetails).verify()

        then:
        noExceptionThrown()
    }
}
