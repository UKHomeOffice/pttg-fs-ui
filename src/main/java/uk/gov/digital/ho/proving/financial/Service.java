package uk.gov.digital.ho.proving.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.financial.domain.ResponseStatus;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping("/financialstatus/v1/")
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Autowired
    private CounterService counterService;

    @RequestMapping(path = "greetings", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity greeting(@RequestParam(value = "accountNumber", required = true) String accountNumber) {

        LOGGER.debug("Greeting: accountNumber - {}", accountNumber);
        counterService.increment("greetings.accountNumber");

        boolean pass = accountNumber.equals("11111111") ? true : false;

        return new ResponseEntity<>(
            "{\"meetsFinancialStatusRequirements\": " + pass + "," +
                " \"maintenancePeriodCheckedFrom\": \"1/1/1066\"," +
                " \"maintenancePeriodCheckedTo\": \"1/1/1966\"}",
            HttpStatus.OK);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object missingParamterHandler(MissingServletRequestParameterException exception) {

        LOGGER.debug(exception.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        return buildErrorResponse(headers, "0008", "Missing parameter: " + exception.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ResponseStatus> buildErrorResponse(HttpHeaders headers, String errorCode, String errorMessage, HttpStatus status) {
        ResponseStatus response = new ResponseStatus(errorCode, errorMessage);
        return new ResponseEntity<>(response, headers, status);
    }
}
