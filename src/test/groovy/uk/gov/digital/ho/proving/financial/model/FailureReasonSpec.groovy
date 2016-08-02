package uk.gov.digital.ho.proving.financial.model

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class FailureReasonSpec extends Specification {

    def static lowBalanceFailure = new FailureReason(LocalDate.of(2015, 10, 3), BigDecimal.valueOf(900.00))
    def static recordCountFailure = new FailureReason(27)

    @Unroll
    def "generates meaningful toString instead of just a hash for #failure"() {

        when:
        def output = instance.toString()

        then:
        output.contains(content)

        and:
        !output.contains('FailureReason@')

        where:
        failure              | instance           | content
        "low balance"        | lowBalanceFailure  | "amount=$lowBalanceFailure.amount"
        "not enough records" | recordCountFailure | "recordCount=$recordCountFailure.recordCount"
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(FailureReason).verify()

        then:
        noExceptionThrown()
    }
}
