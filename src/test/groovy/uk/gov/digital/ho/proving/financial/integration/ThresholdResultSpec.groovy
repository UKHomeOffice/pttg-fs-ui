package uk.gov.digital.ho.proving.financial.integration

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.model.CappedValues
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

/**
 * @Author Home Office Digital
 */
class ThresholdResultSpec extends Specification {

    static final String sampleOneFile = "thresholdresult-sample-one.json"

    ObjectMapper mapper = new ServiceConfiguration().getMapper()


    def "Instance should serialize to json"() {

        given:
        def instance = sampleOne

        when:
        def actual = withoutSpaces(mapper.writeValueAsString(instance))

        then:
        actual == stringFromFile(sampleOneFile)
    }


    def "json should deserialize to instance"() {

        given:
        def expected = sampleOne
        def sampleOneJson = stringFromFile(sampleOneFile)

        when:
        def actual = mapper.readValue(sampleOneJson, ThresholdResult.class)

        then:
        actual == expected
    }

    def "generates meaningful toString instead of just a hash"() {

        given:
        def sample = sampleOne

        when:
        def output = sample.toString()

        then:
        output.contains("threshold=$sample.threshold")

        and:
        !output.contains('ThresholdResult@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(ThresholdResult).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }


    def sampleOne = new ThresholdResult(
        BigDecimal.valueOf(100),
        new CappedValues("1265.00", 9),
        new ResponseDetails("200", "OK"))

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
