package uk.gov.digital.ho.proving.financial;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.financial.exception.AccountNotFoundException;
import uk.gov.digital.ho.proving.financial.exception.InvalidRequestParameterException;
import uk.gov.digital.ho.proving.financial.exception.ServiceProcessingException;
import uk.gov.digital.ho.proving.financial.model.DailyBalanceStatusResponse;
import uk.gov.digital.ho.proving.financial.model.FundingCheckResult;
import uk.gov.digital.ho.proving.financial.model.ThresholdResponse;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping(path = "/pttg/financialstatusservice/v1/accounts/")
@ControllerAdvice
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private static final Pattern SORT_CODE_PATTERN = Pattern.compile("^(?!000000)\\d{6}$");
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^(?!00000000)\\d{8}$");

    @Value("${daily-balance.days-to-check}")
    private int daysToCheck;

    @Autowired
    private ApiUrls apiUrls;

    private Client client = getClient();

    @RequestMapping(path = "{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @PathVariable("accountNumber") String accountNumber,
        @PathVariable("sortCode") String sortCode,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(value = "innerLondonBorough", required = true) Boolean innerLondonBorough,
        @RequestParam(value = "courseLength", required = true) int courseLength,
        @RequestParam(value = "totalTuitionFees", required = true) int totalTuitionFees,
        @RequestParam(value = "tuitionFeesAlreadyPaid", required = true) int tuitionFeesAlreadyPaid,
        @RequestParam(value = "accommodationFeesAlreadyPaid", required = true) int accommodationFeesAlreadyPaid
    ) {

        LOGGER.debug("Status for: accountNumber: {}, sortCode: {}", accountNumber, sortCode);

        // todo standardize validation scheme eg use spring / jsr303 @Valid
        if (!SORT_CODE_PATTERN.matcher(sortCode).matches()) {
            LOGGER.debug("Invalid sortcode: {}", sortCode);
            throw new InvalidRequestParameterException("sortCode", "Invalid sort code");
        }

        if (!ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            LOGGER.debug("Invalid accountNumber: {}", accountNumber);
            throw new InvalidRequestParameterException("accountNumber", "Invalid account number");
        }

        // todo bundle all these params into a class
        return checkFinancialStatus(accountNumber, sortCode, toDate, innerLondonBorough, courseLength, totalTuitionFees, tuitionFeesAlreadyPaid, accommodationFeesAlreadyPaid);
    }

    private ResponseEntity checkFinancialStatus(String accountNumber,
                                                String sortCode,
                                                LocalDate toDate,
                                                Boolean innerLondonBorough,
                                                int courseLength,
                                                int totalTuitionFees,
                                                int tuitionFeesAlreadyPaid,
                                                int accommodationFeesAlreadypaid) {

        BigDecimal totalFundsRequired = getThreshold(innerLondonBorough, courseLength, totalTuitionFees, tuitionFeesAlreadyPaid, accommodationFeesAlreadypaid);

        return getDailyBalanceStatus(accountNumber, sortCode, toDate, totalFundsRequired);
    }

    // todo wrap params into request class
    // todo extract httputils
    // todo introduce response data class
    // todo use optional rather than exceptions

    private BigDecimal getThreshold(Boolean innerLondonBorough, int courseLength, int totalTuitionFees, int tuitionFeesAlreadyPaid, int accommodationFeesAlreadypaid) {

        WebResource thresholdResource = client.resource(apiUrls.thresholdUrlFor(
            innerLondonBorough,
            courseLength,
            totalTuitionFees,
            tuitionFeesAlreadyPaid,
            accommodationFeesAlreadypaid));

        try {
            return thresholdCalculationFor(thresholdResource);

        } catch (ServiceProcessingException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error processing request via FSS API: {}", e.getMessage());
            throw new ServiceProcessingException("0000", "Unknown error processing request via FSS API: " + e.getMessage());
        }
    }

    private BigDecimal thresholdCalculationFor(WebResource webResource) {

        ClientResponse clientResponse = webResource
            .header("accept", MediaType.APPLICATION_JSON)
            .header("content-type", MediaType.APPLICATION_JSON)
            .get(ClientResponse.class);

        if (clientResponse.getStatus() != (Response.Status.OK.getStatusCode())) {
            LOGGER.error("Failure at FSS API with status: {}", clientResponse.getStatus());
            throw new ServiceProcessingException("0005", "Failure at FSS API with status: " + clientResponse.getStatus());

        }

        ThresholdResponse apiResult = clientResponse.getEntity(ThresholdResponse.class);
        LOGGER.debug("Received threshold result: {}", apiResult.toString());

        return apiResult.getThreshold();
    }

    private ResponseEntity getDailyBalanceStatus(String accountNumber, String sortCode, LocalDate toDate, BigDecimal totalFundsRequired) {

        WebResource dailyBalanceResource = client.resource(apiUrls.dailyBalanceStatusUrlFor(
            accountNumber,
            sortCode,
            totalFundsRequired,
            toDate.minusDays(daysToCheck - 1),
            toDate));

        try {
            return fundingCheckResultFor(dailyBalanceResource);

        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new FundingCheckResult(sortCode, accountNumber), HttpStatus.NOT_FOUND);
        } catch (ServiceProcessingException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error processing request via FSS API: {}", e.getMessage());
            throw new ServiceProcessingException("0000", "Unknown error processing request via FSS API: " + e.getMessage());
        }
    }


    private ResponseEntity fundingCheckResultFor(WebResource webResource) {

        ClientResponse clientResponse = webResource
            .header("accept", MediaType.APPLICATION_JSON)
            .header("content-type", MediaType.APPLICATION_JSON)
            .get(ClientResponse.class);

        if (clientResponse.getStatus() != (Response.Status.OK.getStatusCode())) {
            if (clientResponse.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new AccountNotFoundException("00XX", "Account details not found: " + clientResponse.getStatus());
            } else {
                LOGGER.error("Failure at FSS API with status: {}", clientResponse.getStatus());
                throw new ServiceProcessingException("0005", "Failure at FSS API with status: " + clientResponse.getStatus());
            }
        }

        DailyBalanceStatusResponse apiResult = clientResponse.getEntity(DailyBalanceStatusResponse.class);
        LOGGER.debug("Received dailybalancestatus result: {}", apiResult.toString());

        return new ResponseEntity<>(new FundingCheckResult(apiResult), HttpStatus.OK);
    }


    private Client getClient() {
        Client c = Client.create();
        c.setConnectTimeout(10000);
        return c;
    }


}
