package uk.gov.digital.ho.proving.financial.integration

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.model.FailureReason
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class DailyBalanceStatusResultSpec extends Specification {

    static final String sampleOneFile = "dailybalancestatusresult-sample-one.json"
    static final String sampleTwoFile = "dailybalancestatusresult-sample-two.json"
    static final String sampleThreeFile = "dailybalancestatusresult-sample-three.json"
    public static final String ACCOUNT_HOLDER_NAME = "Ray Purchase"

    ObjectMapper mapper = new ServiceConfiguration().getMapper()


    @Unroll
    def "Instance should serialize to json in #fileName"() {

        when:
        def actual = removeLeadingOrTrailingSpaces(mapper.writeValueAsString(instance))

        then:
        actual == stringFromFile(fileName)

        where:
        instance    | fileName
        sampleOne   | sampleOneFile
        sampleTwo   | sampleTwoFile
        sampleThree | sampleThreeFile
    }

    @Unroll
    def "json from #fileName should deserialize to instance"() {

        given:
        def json = stringFromFile(fileName)

        when:
        def actual = mapper.readValue(json, DailyBalanceStatusResult.class)

        then:
        actual == expected

        where:
        expected    | fileName
        sampleOne   | sampleOneFile
        sampleTwo   | sampleTwoFile
        sampleThree | sampleThreeFile
    }

    def "generates meaningful toString instead of just a hash"() {

        given:
        def sample = sampleOne

        when:
        def output = sample.toString()

        then:
        output.contains("pass=$sample.pass")

        and:
        !output.contains('DailyBalanceStatusResult@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(DailyBalanceStatusResult).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }


    def static FailureReason lowBalanceFailure = new FailureReason(LocalDate.of(2015, 10, 3), BigDecimal.valueOf(100))

    def static FailureReason notEnoughRecordsFailure = new FailureReason(27)

    def static sampleOne =
        new DailyBalanceStatusResult(ACCOUNT_HOLDER_NAME, true, null, new ResponseDetails("200", "OK"))
            .withFromDate(LocalDate.of(2015, 10, 3))
            .withMinimum(BigDecimal.valueOf(100))

    def static sampleTwo =
        new DailyBalanceStatusResult(ACCOUNT_HOLDER_NAME,true, lowBalanceFailure, new ResponseDetails("200", "OK"))
            .withFromDate(LocalDate.of(2015, 10, 3))
            .withMinimum(BigDecimal.valueOf(100))

    def static sampleThree =
        new DailyBalanceStatusResult(ACCOUNT_HOLDER_NAME, true, notEnoughRecordsFailure, new ResponseDetails("200", "OK"))
            .withFromDate(LocalDate.of(2015, 10, 3))
            .withMinimum(BigDecimal.valueOf(100))

    def stringFromFile(String fileName) {
        removeLeadingOrTrailingSpaces(new File("src/test/resources/" + fileName).text)
    }

    def removeLeadingOrTrailingSpaces(String input) {
        input.trim();
    }
}
