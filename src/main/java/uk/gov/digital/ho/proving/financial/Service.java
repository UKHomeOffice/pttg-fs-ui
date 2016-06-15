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
import uk.gov.digital.ho.proving.financial.model.DailyBalanceCheckResponse;
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
    private String daysToCheck;

    private Client client = Client.create();


    @RequestMapping(path = "{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(@PathVariable("accountNumber") String accountNumber,
                                 @PathVariable("sortCode") String sortCode,
                                 @RequestParam(value = "totalFundsRequired", required = true) String totalFundsRequired,
                                 @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {


        LOGGER.debug("Status for: accountNumber: {}, sortCode: {}, totalFundsRequired: {}, endDate: {}",
            accountNumber, sortCode, totalFundsRequired, endDate);

        client.setConnectTimeout(10000);

        WebResource webResource = dailyBalanceCheckUrl(accountNumber, sortCode, totalFundsRequired, endDate);

        // to do handle connection failure errors
        // to do handle invalid/unparseable response error
        // to do I'd do it now but it's a different "feature"

        ClientResponse clientResponse = webResource
            .header("accept", MediaType.APPLICATION_JSON)
            .header("content-type", MediaType.APPLICATION_JSON)
            .get(ClientResponse.class);

        DailyBalanceCheckResponse apiResult = clientResponse.getEntity(DailyBalanceCheckResponse.class);

        LOGGER.debug(apiResult.toString());

        if (clientResponse.getStatusInfo().getStatusCode() != (Response.Status.OK.getStatusCode())) {
            return new ResponseEntity<>(HttpStatus.valueOf(clientResponse.getStatus()));
        }

        return new ResponseEntity<>(new FundingCheckResult(apiResult), HttpStatus.OK);
    }

    private WebResource dailyBalanceCheckUrl(String accountNumber, String sortCode, String totalFundsRequired, LocalDate maintenancePeriodEndDate) {

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiEndpoint)
            .queryParam("threshold", totalFundsRequired)
            .queryParam("applicationRaisedDate", maintenancePeriodEndDate)
            .queryParam("days", daysToCheck)
            .buildAndExpand(sortCode, accountNumber)
            .toUri();

        LOGGER.debug(expanded.toString());

        return client.resource(expanded);
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
