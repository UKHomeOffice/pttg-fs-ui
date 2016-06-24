package uk.gov.digital.ho.proving.financial.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @Author Home Office Digital
 */
@ControllerAdvice
public class ApiExceptionHandler {

    // todo standardize use of exception handlers for input validation?
    // todo establish numbering convention for error message format
    // todo move error code numbers to enum
    // todo what about error response from parameter binding errors not being same as custom validation errors?

    private Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails missingParameterHandler(MissingServletRequestParameterException exception) {
        LOGGER.debug("Missing parameter: " + exception.getMessage());
        return new ResponseDetails("0001", "Missing parameter: " + exception.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails unbindableParameterHandler(MethodArgumentTypeMismatchException exception) {
        LOGGER.debug("Unbindable parameter: " + exception.getMessage());
        return new ResponseDetails("0002", "Invalid parameter type: " + exception.getParameter().getParameterName());
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails invalidParameterHandler(InvalidRequestParameterException exception) {
        LOGGER.debug("Invalid request parameter: " + exception.getMessage());
        return new ResponseDetails("0003", "Invalid parameter format: " + exception.getParameterName());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails constraintValidationHandler(ConstraintViolationException exception) {
        LOGGER.debug("Constraint violation: " + exception.getMessage());
        String detail = exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(","));
        return new ResponseDetails("0004", "Invalid parameter: " + detail);
    }

    @ExceptionHandler(ServiceProcessingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDetails serverProcessingFault(ServiceProcessingException exception) {
        LOGGER.debug("Server processing fault: " + exception.getMessage());
        return new ResponseDetails(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails bindException(BindException exception) {LOGGER.debug("exception: " + exception.getMessage());

        LOGGER.debug("Binding exception: " + exception.getMessage());

        // todo clean way of handling binding errors

        FieldError fieldError = exception.getFieldErrors().get(0);
        String fieldName = fieldError.getField().toString();
        String problem = fieldError.getDefaultMessage();

        String code = problem.equalsIgnoreCase("missing parameter") ? "0001" : "0003";

        return new ResponseDetails(code, problem + ": " + fieldName);
    }
}
