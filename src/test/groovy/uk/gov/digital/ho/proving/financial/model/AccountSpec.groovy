package uk.gov.digital.ho.proving.financial.model

import spock.lang.Specification
import nl.jqno.equalsverifier.*

/**
 * @Author Home Office Digital
 */
class AccountSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new Account("112233", "12345678")

        when:
        def output = instance.toString()

        then:
        output.contains("sortCode='$instance.sortCode'")

        and:
        !output.contains('Account@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(Account).verify()

        then:
        noExceptionThrown()
    }
}
