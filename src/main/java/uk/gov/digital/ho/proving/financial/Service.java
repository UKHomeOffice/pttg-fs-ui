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
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse;
import uk.gov.digital.ho.proving.financial.exception.AccountNotFoundException;
import uk.gov.digital.ho.proving.financial.exception.ServiceProcessingException;
import uk.gov.digital.ho.proving.financial.integration.ApiUrls;
import uk.gov.digital.ho.proving.financial.integration.DailyBalanceStatusResult;
import uk.gov.digital.ho.proving.financial.integration.ThresholdResult;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.Course;
import uk.gov.digital.ho.proving.financial.model.Maintenance;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;

import static uk.gov.digital.ho.proving.financial.model.ResponseDetails.notFoundResponseDetails;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping(path = "/pttg/financialstatusservice/v1/accounts/")
@ControllerAdvice
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Value("${daily-balance.days-to-check}")
    private int daysToCheck;

    @Autowired
    private ApiUrls apiUrls;

    private Client client = getClient();


    @RequestMapping(path = "{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @Valid Account account,
        @Valid Course course,
        @Valid Maintenance maintenance,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        LOGGER.debug("Status for: account: {}, course: {}, maintenance: {}, toDate: {}", account, course, maintenance, toDate);
        return checkFinancialStatus(account, toDate, course, maintenance);
    }

    private ResponseEntity checkFinancialStatus(Account account, LocalDate toDate, Course course, Maintenance maintenance) {

        BigDecimal totalFundsRequired = getThreshold(course, maintenance);

        return getDailyBalanceStatus(account, toDate, totalFundsRequired);
    }

    // todo extract httputils
    // todo introduce response data class
    // todo use optional rather than exceptions

    private BigDecimal getThreshold(Course course, Maintenance maintenance) {
        WebResource thresholdResource = client.resource(apiUrls.thresholdUrlFor(course, maintenance));

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

        ThresholdResult apiResult = clientResponse.getEntity(ThresholdResult.class);
        LOGGER.debug("Received threshold result: {}", apiResult.toString());

        return apiResult.getThreshold();
    }

    private ResponseEntity getDailyBalanceStatus(Account account, LocalDate toDate, BigDecimal totalFundsRequired) {

        WebResource dailyBalanceResource = client.resource(apiUrls.dailyBalanceStatusUrlFor(
            account,
            totalFundsRequired,
            toDate.minusDays(daysToCheck - 1),
            toDate));

        try {
            return fundingCheckResultFor(dailyBalanceResource);

        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(new FundingCheckResponse(notFoundResultFor(account)), HttpStatus.NOT_FOUND);
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

        DailyBalanceStatusResult apiResult = clientResponse.getEntity(DailyBalanceStatusResult.class);
        LOGGER.debug("Received dailybalancestatus result: {}", apiResult.toString());

        return new ResponseEntity<>(new FundingCheckResponse(apiResult), HttpStatus.OK);
    }

    private DailyBalanceStatusResult notFoundResultFor(Account account) {
        return new DailyBalanceStatusResult(account, null, null, null, false, notFoundResponseDetails());
    }


    private Client getClient() {
        Client c = Client.create();
        c.setConnectTimeout(10000);
        return c;
    }


}
