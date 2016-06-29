package uk.gov.digital.ho.proving.financial.integration

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.integration.DailyBalanceStatusResult
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class DailyBalanceStatusResultSpec extends Specification {

    static final String sampleOneFile = "dailybalancestatusresult-sample-one.json"

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
        def actual = mapper.readValue(sampleOneJson, DailyBalanceStatusResult.class)

        then:
        actual == expected
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


    def sampleOne =
        new DailyBalanceStatusResult(true, new ResponseDetails("200", "OK"))
            .withFromDate(LocalDate.of(2015, 10, 3))
            .withMinimum(BigDecimal.valueOf(100))

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
