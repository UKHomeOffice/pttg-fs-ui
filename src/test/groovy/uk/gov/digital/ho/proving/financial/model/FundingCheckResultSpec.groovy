package uk.gov.digital.ho.proving.financial.model

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse

import java.time.LocalDate


/**
 * @Author Home Office Digital
 */
class FundingCheckResultSpec extends Specification {

    public static final String sampleOneFile = "fundingcheckresult-sample-one.json"
    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    def "Instance should serialize to json"() {

        given:
        def instance = sampleOne

        when:
        def actual = withoutSpaces(mapper.writeValueAsString(instance))

        println actual

        then:
        actual == stringFromFile(sampleOneFile)
    }


    def "json should deserialize to instance"() {

        given:
        def expected = sampleOne
        def sampleOneJson = stringFromFile(sampleOneFile)

        when:
        def actual = mapper.readValue(sampleOneJson, FundingCheckResponse.class)

        then:
        actual == expected
    }

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new FundingCheckResponse(false, null, null)

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

    def sampleOne = new FundingCheckResponse(
        true,
        LocalDate.of(2015, 10, 3),
        BigDecimal.valueOf(100))

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
