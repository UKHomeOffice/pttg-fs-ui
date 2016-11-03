package uk.gov.digital.ho.proving.financial.model

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

/**
 * @Author Home Office Digital
 */
class CappedValuesSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new CappedValues(200.00, 9, 3)

        when:
        def output = instance.toString()

        then:
        output.contains("courseLength=$instance.courseLength")

        and:
        !output.contains('CappedValues@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(CappedValues).verify()

        then:
        noExceptionThrown()
    }
}
