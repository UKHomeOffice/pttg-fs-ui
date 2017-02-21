package uk.gov.digital.ho.proving.financial.exception

import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import spock.lang.Specification
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

/**
 * @Author Home Office Digital
 */
class ServiceExceptionHandlerSpec extends Specification {

    ServiceExceptionHandler handler

    def setup() {
        handler = new ServiceExceptionHandler()
    }

    def 'extracts parameter name from MissingServletRequestParameterException'() {

        given:
        MissingServletRequestParameterException e = new MissingServletRequestParameterException("missing-parameter-name", "type")

        when:
        ResponseDetails response = handler.missingParameterHandler(e)

        then:
        response.message.contains("missing-parameter-name")
    }

    def 'extracts parameter name from MethodArgumentTypeMismatchException'() {

        given:
        MethodArgumentTypeMismatchException e = Mock()
        MethodParameter p = Mock()
        e.getParameter() >> p

        when:
        ResponseDetails response = handler.unbindableParameterHandler(e)

        then:
        1 * p.getParameterName() >> "missing-parameter-name"
        response.message.contains("missing-parameter-name")
    }

    def 'extracts parameter name from InvalidRequestParameterException'() {

        given:
        InvalidRequestParameterException e = Mock()

        when:
        ResponseDetails response = handler.invalidParameterFormatHandler(e)

        then:
        1 * e.getParameterName() >> "missing-parameter-name"
        response.message.contains("missing-parameter-name")
    }

    def 'extracts all messages from ConstraintViolationException'() {

        given:
        ConstraintViolation one = Mock()
        ConstraintViolation two = Mock()

        ConstraintViolationException e = Mock()
        e.getConstraintViolations() >> [one, two]
        one.getMessage() >> 'message 1'
        two.getMessage() >> 'message 2'

        when:
        ResponseDetails response = handler.constraintValidationHandler(e)

        then:
        response.message.contains("message 1, message 2")
    }

    def 'gives Internal server error message for ServiceProcessingException'() {

        given:
        ServiceProcessingException e = Mock()

        when:
        ResponseDetails response = handler.serverProcessingFault(e)

        then:
        response.message.contains("Internal server error")
    }

    def 'converts RestServiceExceptions emerging from API'() {

        given:
        RestServiceException e = Mock()
        e.getStatusCode() >> apiStatusCode

        when:
        ResponseEntity entity = handler.restApiResponseError(e)

        then:
        entity.statusCode == convertedStatusCode
        entity.toString().contains(message)

        where:
        apiStatusCode                    | convertedStatusCode              | message
        HttpStatus.NOT_FOUND             | HttpStatus.NOT_FOUND             | '{}'
        HttpStatus.INTERNAL_SERVER_ERROR | HttpStatus.INTERNAL_SERVER_ERROR | 'Error at FSS API server'
        HttpStatus.BAD_GATEWAY           | HttpStatus.INTERNAL_SERVER_ERROR | 'API response status: 502'
        HttpStatus.BAD_REQUEST           | HttpStatus.BAD_REQUEST           | 'Bad request to FSS API server'
    }


    def 'extracts parameter name from BindException'() {

        given:
        FieldError field = Mock()
        field.getField() >> "field name"
        field.getDefaultMessage() >> "message"

        BindException e = Mock()
        e.getFieldErrors() >> [field]


        when:
        ResponseDetails response = handler.bindException(e)

        then:
        response.message.contains("field name")
    }


    def 'gives a general error message for unhandled exception types'() {

        given:
        Exception e = Mock()

        when:
        ResponseDetails response = handler.unknownException(e)

        then:
        response.message.contains("There was an unhandled error")
    }


    def 'handles connection refused exception' () {

        given:
        ResourceAccessException e = Mock()
        ConnectException ce = Mock()

        e.getCause() >> ce
        ce.getMessage() >> 'Connection refused'

        when:
        ResponseDetails response = handler.resourceException(e)

        then:
        response.message.contains("There was a problem connecting to the service")
        response.message.contains('Connection refused')
    }

    def 'handles connection timed out exception' () {

        given:
        ResourceAccessException e = Mock()
        ConnectException ce = Mock()

        e.getCause() >> ce
        ce.getMessage() >> 'Connection timed out'

        when:
        ResponseDetails response = handler.resourceException(e)

        then:
        response.message.contains("There was a problem connecting to the service")
        response.message.contains('Connection timed out')
    }

    def 'handles unknown types of resource access exception' () {

        given:
        ResourceAccessException e = Mock()
        Exception c = Mock()

        e.getCause() >> c

        when:
        ResponseDetails response = handler.resourceException(e)

        then:
        response.message.contains("There was an unknown problem using the service")

    }

}
