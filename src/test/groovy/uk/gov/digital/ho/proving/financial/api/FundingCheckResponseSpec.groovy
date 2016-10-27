package uk.gov.digital.ho.proving.financial.api

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.model.CappedValues
import uk.gov.digital.ho.proving.financial.model.FailureReason

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class FundingCheckResponseSpec extends Specification {

    static final String sampleOneFile = "fundingcheckresponse-sample-one.json"
    static final String sampleTwoFile = "fundingcheckresponse-sample-two.json"
    static final String sampleThreeFile = "fundingcheckresponse-sample-three.json"
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
        def actual = mapper.readValue(json, FundingCheckResponse.class)

        then:
        actual == expected

        where:
        expected  | fileName
        sampleOne | sampleOneFile
        sampleTwo | sampleTwoFile
        sampleThree | sampleThreeFile
    }

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new FundingCheckResponse(false, null, null, null, null, null)

        when:
        def output = instance.toString()

        then:
        output.contains("fundingRequirementMet=$instance.fundingRequirementMet")

        and:
        !output.contains('FundingCheckResponse@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(FundingCheckResponse).verify()

        then:
        noExceptionThrown()
    }

    def static lowBalanceFailure = new FailureReason(LocalDate.of(2015, 10, 3), BigDecimal.valueOf(100))
    def static recordCountFailure = new FailureReason(27)

    def static sampleOne = new FundingCheckResponse(
        true,
        ACCOUNT_HOLDER_NAME,
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100),
        null,
        null
    )

    def static sampleTwo = new FundingCheckResponse(
        true,
        ACCOUNT_HOLDER_NAME,
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100),
        lowBalanceFailure,
        new CappedValues(1265.00, 9, 3)
    )

    def static sampleThree = new FundingCheckResponse(
        true,
        ACCOUNT_HOLDER_NAME,
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100),
        recordCountFailure,
        new CappedValues(1265.00, 9, 3)
    )

    def stringFromFile(String fileName) {
        removeLeadingOrTrailingSpaces(new File("src/test/resources/" + fileName).text)
    }

    def removeLeadingOrTrailingSpaces(String input) {
        input.trim()
    }
}
