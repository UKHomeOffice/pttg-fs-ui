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

    def 'throws exception containing response status code' (){

        given:
        response.statusCode >> HttpStatus.NOT_FOUND

        when:
        handler.handleError(response)

        then:
        RestServiceException rse = thrown()
        rse.getStatusCode() == HttpStatus.NOT_FOUND
    }
}
