package uk.gov.digital.ho.proving.financial.model

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.ServiceConfiguration

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class DailyBalanceStatusResponseSpec extends Specification {

    ObjectMapper mapper = new ServiceConfiguration().getMapper()


    def "Instance should serialize to json"() {

        given:
        def instance = sampleOne

        when:
        def actual = withoutSpaces(mapper.writeValueAsString(instance))

        then:
        actual == stringFromFile("dailybalancestatusresponse-sample-one.json")
    }


    def "json should deserialize to instance"() {

        given:
        def expected = sampleOne
        def sampleOneJson = stringFromFile("dailybalancestatusresponse-sample-one.json")

        when:
        def actual = mapper.readValue(sampleOneJson, DailyBalanceStatusResponse.class)

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
        !output.contains('DailyBalanceStatusResponse@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(DailyBalanceStatusResponse).verify()

        then:
        noExceptionThrown()
    }


    def sampleOne = new DailyBalanceStatusResponse(
        new Account("112233", "12345678"),
        LocalDate.of(2015, 10, 3),
        LocalDate.of(2015, 10, 30),
        BigDecimal.valueOf(100),
        true,
        new ResponseDetails("200", "OK"))

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
