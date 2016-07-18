package uk.gov.digital.ho.proving.financial.integration

import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.exception.RestServiceException

/**
 * @Author Home Office Digital
 */
class RestServiceErrorHandlerSpec extends Specification {

    RestServiceErrorHandler handler
    ClientHttpResponse response

    def setup() {
        handler = new RestServiceErrorHandler()
        response = Mock(ClientHttpResponse)
    }

    @Unroll
    def 'considers http status #statusCode as error? #isError'() {

        given:
        response.statusCode >> statusCode

        when:
        def error = handler.hasError(response)

        then:
        error == isError

        where:
        statusCode                       | isError
        HttpStatus.NOT_FOUND             | true
        HttpStatus.BAD_REQUEST           | true
        HttpStatus.INTERNAL_SERVER_ERROR | true
        HttpStatus.OK                    | false
    }

    def 'throws exception containing response status code'() {

        given:
        response.statusCode >> HttpStatus.NOT_FOUND

        when:
        handler.handleError(response)

        then:
        RestServiceException rse = thrown()
        rse.getStatusCode() == HttpStatus.NOT_FOUND
    }

    def 'extracts error status message and code, if present, when error is a client error'() {

        def jsonErrorResponse = "{\n" +
            "    \"threshold\" : null,\n" +
            "    \"status\" : {\n" +
            "        \"code\" : \"0101\",\n" +
            "        \"message\" : \"Parameter error: Invalid courseLength\"\n" +
            "    }\n" +
            "}"

        given:
        response.statusCode >> HttpStatus.BAD_REQUEST
        response.getBody() >> new ByteArrayInputStream(jsonErrorResponse.getBytes("UTF-8"))

        when:
        handler.handleError(response)

        then:
        RestServiceException rse = thrown()

        with(rse) {
            getStatusCode() == HttpStatus.BAD_REQUEST
            getReasonMessage() == "Parameter error: Invalid courseLength"
            getReasonCode() == "0101"
        }

    }

    def 'just uses response status code if error status message and code not present when error is a client error'() {

        given:

        response.statusCode >> HttpStatus.BAD_REQUEST
        response.getBody() >> new ByteArrayInputStream(responseBodyString.getBytes("UTF-8"))

        when:
        handler.handleError(response)

        then:
        RestServiceException rse = thrown()

        with(rse) {
            getStatusCode() == HttpStatus.BAD_REQUEST
            getReasonMessage() == HttpStatus.BAD_REQUEST.reasonPhrase
            getReasonCode() == ""
        }

        where:
        responseBodyString << ["", "bad-json", "{\"wrong\":\"json\""]
    }

    def 'just uses response status code if no response body when error is a client error'() {

        given:

        response.statusCode >> HttpStatus.BAD_REQUEST
        response.getBody() >> null

        when:
        handler.handleError(response)

        then:
        RestServiceException rse = thrown()

        with(rse) {
            getStatusCode() == HttpStatus.BAD_REQUEST
            getReasonMessage() == HttpStatus.BAD_REQUEST.reasonPhrase
            getReasonCode() == ""
        }
    }
}
