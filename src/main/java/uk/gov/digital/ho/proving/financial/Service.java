package uk.gov.digital.ho.proving.financial;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.financial.exception.AccountNotFoundException;
import uk.gov.digital.ho.proving.financial.exception.InvalidRequestParameterException;
import uk.gov.digital.ho.proving.financial.exception.ServiceProcessingException;
import uk.gov.digital.ho.proving.financial.model.DailyBalanceStatusResponse;
import uk.gov.digital.ho.proving.financial.model.FundingCheckResult;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
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

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.endpoint}")
    private String apiEndpoint;

    @Value("${daily-balance.days-to-check}")
    private int daysToCheck;

    private Client client = getClient();


    @RequestMapping(path = "{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @PathVariable("accountNumber") String accountNumber,
        @PathVariable("sortCode") String sortCode,
        @RequestParam(value = "totalFundsRequired", required = true) BigDecimal totalFundsRequired,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LOGGER.debug("Status for: accountNumber: {}, sortCode: {}, totalFundsRequired: {}, toDate: {}",
            accountNumber, sortCode, totalFundsRequired, toDate);

        // todo standardize validation scheme eg use spring / jsr303 @Valid
        if (!SORT_CODE_PATTERN.matcher(sortCode).matches()) {
            LOGGER.debug("Invalid sortcode: {}", sortCode);
            throw new InvalidRequestParameterException("sortCode", "Invalid sort code");
        }

        if (!ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            LOGGER.debug("Invalid accountNumber: {}", accountNumber);
            throw new InvalidRequestParameterException("accountNumber", "Invalid account number");
        }

        WebResource webResource = dailyBalanceStatusUrlFor(accountNumber, sortCode, totalFundsRequired, toDate.minusDays(daysToCheck - 1), toDate);

        try {
            return fundingCheckResultFor(webResource);

        } catch (AccountNotFoundException e){
            return new ResponseEntity<>(new FundingCheckResult(sortCode, accountNumber), HttpStatus.NOT_FOUND);
        } catch (ServiceProcessingException e){
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error processing request via FSS API: {}", e.getMessage());
            throw new ServiceProcessingException("0000", "Unknown error processing request via FSS API: " + e.getMessage());
        }
    }

    private WebResource dailyBalanceStatusUrlFor(String accountNumber, String sortCode, BigDecimal totalFundsRequired, LocalDate from, LocalDate to) {

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiEndpoint)
            .queryParam("minimum", totalFundsRequired.toPlainString())
            .queryParam("fromDate", from)
            .queryParam("toDate", to)
            .buildAndExpand(sortCode, accountNumber)
            .toUri();

        LOGGER.debug(expanded.toString());

        return client.resource(expanded);
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
