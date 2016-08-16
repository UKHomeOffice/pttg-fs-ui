package uk.gov.digital.ho.proving.financial.model

import spock.lang.Specification
import nl.jqno.equalsverifier.*

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class AccountSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new Account("112233", "12345678", LocalDate.of(1980,1,1))

        when:
        def output = instance.toString()

        then:
        output.contains("sortCode='$instance.sortCode'")

        and:
        !output.contains('Account@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(Account).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}
