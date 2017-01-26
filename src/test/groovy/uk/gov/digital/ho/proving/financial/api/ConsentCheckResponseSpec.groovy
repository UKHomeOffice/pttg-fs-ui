package uk.gov.digital.ho.proving.financial.api

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.ServiceConfiguration

/**
 * @Author Home Office Digital
 */
class ConsentCheckResponseSpec extends Specification {

    static final String sampleJsonFile = "consentcheckresponse-sample.json"
    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    def "object should serialize to json"() {
        given:
        def sampleObject = new ConsentCheckResponse("SUCCESS", new ConsentStatus("200", "OK"))

        when:
        def actualJson = removeLeadingOrTrailingSpaces(mapper.writeValueAsString(sampleObject))

        then:
        actualJson == stringFromFile(sampleJsonFile)

    }

    def "json should deserialize to object"() {
        given:
        def json = stringFromFile(sampleJsonFile)

        when:
        def actualObject = mapper.readValue(json, ConsentCheckResponse)

        then:
        def expectedObject = new ConsentCheckResponse("SUCCESS", new ConsentStatus("200", "OK"))
        actualObject == expectedObject
    }

    def stringFromFile(String fileName) {
        removeLeadingOrTrailingSpaces(new File("src/test/resources/" + fileName).text)
    }

    def removeLeadingOrTrailingSpaces(String input) {
        input.trim()
    }
}
