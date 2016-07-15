package uk.gov.digital.ho.proving.financial.model

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

import static java.math.BigDecimal.ONE

/**
 * @Author Home Office Digital
 */
class MaintenanceSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new Maintenance(ONE, ONE, ONE, 1)

        when:
        def output = instance.toString()

        then:
        output.contains("totalTuitionFees=$instance.totalTuitionFees")

        and:
        !output.contains('Maintenance@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(Account).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}
