package uk.gov.digital.ho.proving.financial.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

/**
 * @Author Home Office Digital
 */
@ControllerAdvice
public class ApiExceptionHandler {

    // todo standardize use of exception handlers for input validation?
    // todo establish numbering convention for error message format
    // todo what about error response from parameter binding errors not being same as custom validation errors?

    private Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails missingParameterHandler(MissingServletRequestParameterException exception) {
        LOGGER.debug(exception.getMessage());
        return new ResponseDetails("0001", "Missing parameter: " + exception.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails unbindableParameterHandler(MethodArgumentTypeMismatchException exception) {
        LOGGER.debug(exception.getMessage());
        return new ResponseDetails("0002", "Invalid parameter type: " + exception.getParameter().getParameterName());
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDetails invalidParameterHandler(InvalidRequestParameterException exception) {
        LOGGER.debug(exception.getMessage());
        return new ResponseDetails("0003", "Invalid parameter format: " + exception.getParameterName());
    }

    @ExceptionHandler(ServiceProcessingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseDetails serverProcessingFault(ServiceProcessingException exception) {
        LOGGER.debug(exception.getMessage());
        return new ResponseDetails(exception.getCode(), exception.getMessage());
    }
}
