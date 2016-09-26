package uk.gov.digital.ho.proving.financial.exception;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.ConnectException;
import java.util.stream.Collectors;

import static net.logstash.logback.marker.Markers.append;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.gov.digital.ho.proving.financial.model.ErrorCode.*;

/**
 * @Author Home Office Digital
 */
@ControllerAdvice
public class ServiceExceptionHandler {

    private Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails missingParameterHandler(MissingServletRequestParameterException exception) {
        String name = exception.getParameterName();
        LOGGER.debug(append("errorCode", "0001"), "Missing parameter: " + exception.getMessage());
        return new ResponseDetails(MISSING_PARAMETER.getCode(), MISSING_PARAMETER.getMessage() + exception.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails unbindableParameterHandler(MethodArgumentTypeMismatchException exception) {
        LOGGER.debug(append("errorCode", "0002"), "Unbindable parameter: " + exception.getMessage());
        return new ResponseDetails(INVALID_PARAMETER_TYPE.getCode(), INVALID_PARAMETER_TYPE.getMessage() + exception.getParameter().getParameterName());
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails invalidParameterFormatHandler(InvalidRequestParameterException exception) {
        LOGGER.debug(append("errorCode", "0003"), "Invalid parameter format: " + exception.getMessage());
        return new ResponseDetails(INVALID_PARAMETER_FORMAT.getCode(), INVALID_PARAMETER_FORMAT.getMessage() + exception.getParameterName());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails constraintValidationHandler(ConstraintViolationException exception) {
        LOGGER.debug(append("errorCode", "0004"), "Constraint violation: " + exception.getMessage());
        String detail = exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        return new ResponseDetails(INVALID_PARAMETER_VALUE.getCode(), INVALID_PARAMETER_VALUE.getMessage() + detail);
    }

    @ExceptionHandler(ServiceProcessingException.class)
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDetails serverProcessingFault(ServiceProcessingException exception) {
        LOGGER.debug(append("errorCode", "0005"), "Server processing fault: " + exception.getMessage());
        return new ResponseDetails(INTERNAL_ERROR);
    }

    @ExceptionHandler(RestServiceException.class)
    public ResponseEntity<?> restApiResponseError(RestServiceException exception) {
        LOGGER.debug("Rest service exception, status: {}", exception.getStatusCode());

        switch (exception.getStatusCode()) {

            case BAD_REQUEST:
                LOGGER.error(append("errorCode", "0007"), "Rest service exception - bad request, which means that there may be a mismatch between UI and API");
                return new ResponseEntity<>(new ResponseDetails(API_CLIENT_ERROR), INTERNAL_SERVER_ERROR);

            case INTERNAL_SERVER_ERROR:
                LOGGER.error(append("errorCode", "0006"), "Rest service exception - internal server error");
                return new ResponseEntity<>(new ResponseDetails(API_SERVER_ERROR), INTERNAL_SERVER_ERROR);

            case NOT_FOUND:
                LOGGER.warn(append("errorCode", "0009"), "Rest service exception - not found");
                // Without the empty json response body, Phantom JS fails to propagate the response status through
                // the angular restservice error handler and into the angular controller for routing to the
                // 'no record' page. I don't know why. It works fine without the response body in at least
                // Chrome and ChromeDriver.
                return new ResponseEntity<>("{}", HttpStatus.NOT_FOUND);

            default:
                LOGGER.error(append("errorCode", "0005"), "Rest service exception: {}", exception.getMessage());
                return new ResponseEntity<ResponseDetails>(
                    new ResponseDetails(INTERNAL_ERROR.getCode(), "API response status: " + exception.getStatusCode()), INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails bindException(BindException exception) {
        LOGGER.debug(append("errorCode", "0003"), "Binding exception: " + exception.getMessage());

        if (exception.getFieldErrors() == null || exception.getFieldErrors().size() <= 0) {
            return new ResponseDetails(INVALID_PARAMETER_VALUE);
        }

        FieldError fieldError = exception.getFieldErrors().get(0);
        String fieldName = fieldError.getField().toString();
        String problem = fieldError.getDefaultMessage();

        String code = problem.equalsIgnoreCase("missing parameter") ? "0001" : "0003";

        return new ResponseDetails(code, problem + ": " + fieldName);
    }


    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDetails resourceException(ResourceAccessException exception) {
        LOGGER.debug(append("errorCode", "0008"), "Resource access exception: " + exception.getMessage());

        if (exception.getCause() instanceof ConnectException || exception.getCause() instanceof ConnectTimeoutException) {
            LOGGER.debug("Connection exception: " + exception.getCause().getMessage());
            return new ResponseDetails(API_CONNECTION_ERROR.getCode(), "There was a problem connecting to the service: " + exception.getCause().getMessage());
        }

        return new ResponseDetails(API_CONNECTION_ERROR.getCode(), "There was an unknown problem using the service: " + exception.getCause().getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDetails unknownException(Exception exception) {
        LOGGER.debug(append("errorCode", "0005"), "Unknown exception: {} : {}", exception.getClass(), exception.getMessage());
        return new ResponseDetails(INTERNAL_ERROR.getCode(), "There was an unhandled error: " + exception.getMessage());
    }
}

