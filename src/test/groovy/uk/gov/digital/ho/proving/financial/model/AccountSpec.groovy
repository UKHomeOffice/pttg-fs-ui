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
        EqualsVerifier.forClass(Account).suppress(Warning.NONFINAL_FIELDS).verify() // todo remove suppression when can make spring binding behave with immutable class

        then:
        noExceptionThrown()
    }
}
