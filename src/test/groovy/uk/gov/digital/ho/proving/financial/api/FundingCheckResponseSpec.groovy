package uk.gov.digital.ho.proving.financial.api

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.model.CappedValues

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class FundingCheckResponseSpec extends Specification {

    static final String sampleOneFile = "fundingcheckresponse-sample-one.json"
    static final String sampleTwoFile = "fundingcheckresponse-sample-two.json"

    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    def "Instance should serialize to json"() {

        when:
        def actual = withoutSpaces(mapper.writeValueAsString(instance))

        then:
        actual == stringFromFile(fileName)

        where:
        instance  | fileName
        sampleOne | sampleOneFile
        sampleTwo | sampleTwoFile
    }

    def "json should deserialize to instance"() {

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

    def static sampleOne = new FundingCheckResponse(
        true,
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100),
        null,
        null,
        null
    )

    def static sampleTwo = new FundingCheckResponse(
        true,
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100),
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100),
        new CappedValues("1265.00", 9)
    )

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
