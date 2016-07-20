package uk.gov.digital.ho.proving.financial.integration

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.ServiceConfiguration
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import java.time.LocalDate

/**
 * @Author Home Office Digital
 */
class DailyBalanceStatusResultSpec extends Specification {

    static final String sampleOneFile = "dailybalancestatusresult-sample-one.json"
    static final String sampleTwoFile = "dailybalancestatusresult-sample-two.json"

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
        def sampleOneJson = stringFromFile(fileName)

        when:
        def actual = mapper.readValue(sampleOneJson, DailyBalanceStatusResult.class)

        then:
        actual == expected

        where:
        expected  | fileName
        sampleOne | sampleOneFile
        sampleTwo | sampleTwoFile
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


    def static sampleOne =
        new DailyBalanceStatusResult(true, null, null, new ResponseDetails("200", "OK"))
            .withFromDate(LocalDate.of(2015, 10, 3))
            .withMinimum(BigDecimal.valueOf(100))

    def static sampleTwo =
        new DailyBalanceStatusResult(true, LocalDate.of(2015, 10, 3), BigDecimal.valueOf(100), new ResponseDetails("200", "OK"))
            .withFromDate(LocalDate.of(2015, 10, 3))
            .withMinimum(BigDecimal.valueOf(100))

    def stringFromFile(String fileName) {
        withoutSpaces(new File("src/test/resources/" + fileName).text)
    }

    def withoutSpaces(String input) {
        input.replaceAll('\\s+', '')
    }
}
