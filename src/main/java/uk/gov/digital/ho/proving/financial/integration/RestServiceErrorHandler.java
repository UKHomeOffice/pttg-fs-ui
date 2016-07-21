package uk.gov.digital.ho.proving.financial.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uk.gov.digital.ho.proving.financial.ServiceConfiguration;
import uk.gov.digital.ho.proving.financial.exception.RestServiceException;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @Author Home Office Digital
 */
@Component
public class RestServiceErrorHandler implements ResponseErrorHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(RestServiceErrorHandler.class);

    private ObjectMapper mapper = new ServiceConfiguration().getMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus.Series series = response.getStatusCode().series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
            || HttpStatus.Series.SERVER_ERROR.equals(series));
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        LOGGER.error("FSS API response error: {} {}", response.getStatusCode(), response.getStatusText());

        HttpStatus statusCode = response.getStatusCode();

        if (statusCode.is4xxClientError()) {
            LOGGER.debug("FSS API reported client error - possible version mismatch?");
            throwClientErrorDetails(response, statusCode);
        }

        throw new RestServiceException(statusCode);
    }

    private void throwClientErrorDetails(ClientHttpResponse response, HttpStatus statusCode) {
        try {
            ResponseDetails details = readResponseDetails(response);
            LOGGER.debug("FSS API reported client error with message: '{}', and code: '{}'", details.getMessage(), details.getCode());

            throw new RestServiceException(statusCode, details.getMessage(), details.getCode());

        } catch (IOException | JSONException e) {
            LOGGER.warn("FSS API reported client error but could not find status.message and status.code");
            throw new RestServiceException(statusCode);

        }
    }

    private ResponseDetails readResponseDetails(ClientHttpResponse response) throws IOException {

        String responseBody = read(response.getBody());

        JSONObject json = new JSONObject(responseBody);

        JSONObject status = json.getJSONObject("status");

        String message = status.getString("message");
        String code = status.getString("code");

        return new ResponseDetails(code, message);
    }

    public static String read(InputStream input) throws IOException {

        if(input == null){
            return "";
        }

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}
