package uk.gov.digital.ho.proving.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.financial.domain.ResponseStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping("/financialstatus/v1/")
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private static Map<String, Double> accountBalances = new HashMap<>();

    @Autowired
    private CounterService counterService;

    @RequestMapping(path = "status", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(@RequestParam(value = "accountNumber", required = true) String accountNumber,
                                 @RequestParam(value = "sortCode", required = true) String sortCode,
                                 @RequestParam(value = "totalFundsRequired", required = true) String totalFundsRequired,
                                 @RequestParam(value = "maintenancePeriodEndDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maintenancePeriodEndDate) {


        LOGGER.debug("Status for: accountNumber - {}, sortCode - {}", accountNumber, sortCode);
        counterService.increment("financial.status.check.servicecall");

        // to do - stop faking
        int threshold = Integer.parseInt(totalFundsRequired);

        boolean pass = (threshold <= 0) ? true : false;
        if (accountBalances.containsKey(accountNumber)) {

            LOGGER.debug("matched accountnumber - balance is {}", accountBalances.get(accountNumber));

            pass = accountBalances.get(accountNumber) >= threshold;
        }


        LocalDate maintenancePeriodFromDate = maintenancePeriodEndDate.minusDays(28);

        return new ResponseEntity<>(
            "{\"meetsFinancialStatusRequirements\": " + pass + "," +
                " \"maintenancePeriodCheckedFrom\": \"" + maintenancePeriodFromDate + "\"," +
                " \"maintenancePeriodCheckedTo\": \"" + maintenancePeriodEndDate + "\"}",
            HttpStatus.OK);
    }

    @RequestMapping(path = "stub", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity stub(@RequestBody AccountSpec account) {

        LOGGER.debug("Account spec: {}", account);

        accountBalances.put(account.accountNumber, account.balance);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static class AccountSpec {
        double balance;
        String accountNumber;

        public AccountSpec() {
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        @Override
        public String toString() {
            return "AccountSpec{" +
                "minimumBalance=" + balance +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
        }
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
