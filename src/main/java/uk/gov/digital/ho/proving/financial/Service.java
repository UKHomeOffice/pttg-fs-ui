package uk.gov.digital.ho.proving.financial;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.financial.model.DailyBalanceStatusResponse;
import uk.gov.digital.ho.proving.financial.model.FundingCheckResult;
import uk.gov.digital.ho.proving.financial.model.ResponseStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping(path = "/pttg/financialstatusservice/v1/accounts/")
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private static Map<String, Double> accountBalances = new HashMap<>();


    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.endpoint}")
    private String apiEndpoint;

    @Value("${daily-balance.days-to-check}")
    private int daysToCheck;

    private Client client = getClient();




    @RequestMapping(path = "{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(@PathVariable("accountNumber") String accountNumber,
                                 @PathVariable("sortCode") String sortCode,
                                 @RequestParam(value = "totalFundsRequired", required = true) String totalFundsRequired,
                                 @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LOGGER.debug("Status for: accountNumber: {}, sortCode: {}, totalFundsRequired: {}, toDate: {}",
            accountNumber, sortCode, totalFundsRequired, toDate);


        WebResource webResource = dailyBalanceStatusUrlFor(accountNumber, sortCode, totalFundsRequired, toDate.minusDays(daysToCheck - 1), toDate);

        try {
            return fundingCheckResultFor(webResource);

        } catch (Exception e) {
            LOGGER.error("Error processing request via FSS API: {}", e.getMessage());
            return buildErrorResponse("0000", "Error processing request via FSS API: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity fundingCheckResultFor(WebResource webResource) {

        ClientResponse clientResponse = webResource
            .header("accept", MediaType.APPLICATION_JSON)
            .header("content-type", MediaType.APPLICATION_JSON)
            .get(ClientResponse.class);

        if (clientResponse.getStatus() != (Response.Status.OK.getStatusCode())) {
            LOGGER.error("Failure at FSS API with status: {}", clientResponse.getStatus());
            return buildErrorResponse("0000", "Failure at FSS API with status: " + clientResponse.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        DailyBalanceStatusResponse apiResult = clientResponse.getEntity(DailyBalanceStatusResponse.class);
        LOGGER.debug("Received dailybalancestatus result: {}", apiResult.toString());

        return new ResponseEntity<>(new FundingCheckResult(apiResult), HttpStatus.OK);
    }

    private WebResource dailyBalanceStatusUrlFor(String accountNumber, String sortCode, String totalFundsRequired, LocalDate from, LocalDate to) {

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiEndpoint)
            .queryParam("minimum", totalFundsRequired)
            .queryParam("fromDate", from)
            .queryParam("toDate", to)
            .buildAndExpand(sortCode, accountNumber)
            .toUri();

        LOGGER.debug(expanded.toString());

        return client.resource(expanded);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object missingParamterHandler(MissingServletRequestParameterException exception) {

        LOGGER.debug(exception.getMessage());

        return buildErrorResponse("0008", "Missing parameter: " + exception.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ResponseStatus> buildErrorResponse(String errorCode, String errorMessage, HttpStatus status) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        ResponseStatus response = new ResponseStatus(errorCode, errorMessage);
        return new ResponseEntity<>(response, headers, status);
    }

    private Client getClient() {
        Client c = Client.create();
        c.setConnectTimeout(10000);
        return c;
    }
}
