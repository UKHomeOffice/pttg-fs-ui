package uk.gov.digital.ho.proving.financial.model

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.ServiceConfiguration

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class FundingCheckResultSpec extends Specification {

    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    def "Instance should serialize to json"() {

        given:
        def instance = sampleOne

        when:
        def actual = withoutSpaces(mapper.writeValueAsString(instance))

        println actual

        then:
        actual == stringFromFile("fundingcheckresult-sample-one.json")
    }


    def "json should deserialize to instance"() {

        given:
        def expected = sampleOne
        def sampleOneJson = stringFromFile("fundingcheckresult-sample-one.json")

        when:
        def actual = mapper.readValue(sampleOneJson, FundingCheckResult.class)

        then:
        actual == expected
    }

    @Unroll
    def "should not format sortCode #sortCode because #invalidBecause"() {

        when:
        def instance = new FundingCheckResult(sortCode, null, false, null, null, null)

        then:
        instance.sortCode == sortCode

        where:
        sortCode  | invalidBecause
        '11223'   | 'too short'
        '1122334' | 'too long'
    }


    @Unroll
    def "should format #sortCode to #formatted for presentation"() {

        when:
        def instance = new FundingCheckResult(sortCode, null, false, null, null, null)

        then:
        instance.sortCode == formatted

        where:
        sortCode | formatted
        '112233' | '11-22-33'
        '002233' | '00-22-33'
        '002200' | '00-22-00'
    }

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new FundingCheckResult("112233", null, false, null, null, null)

        when:
        def output = instance.toString()

        then:
        output.contains("sortCode='$instance.sortCode'")

        and:
        !output.contains('FundingCheckResult@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(FundingCheckResult).verify()

        then:
        noExceptionThrown()
    }

    def sampleOne = new FundingCheckResult("112233",
        "1245678",
        true,
        LocalDate.of(2015, 10, 3),
        LocalDate.of(2015, 10, 30),
        BigDecimal.valueOf(100))

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
