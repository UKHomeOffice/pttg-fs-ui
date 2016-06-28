package uk.gov.digital.ho.proving.financial.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uk.gov.digital.ho.proving.financial.exception.RestServiceException;

import java.io.IOException;

/**
 * @Author Home Office Digital
 */
@Component
public class RestServiceErrorHandler implements ResponseErrorHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(RestServiceErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        LOGGER.error("FSS API response error: {} {}", response.getStatusCode(), response.getStatusText());
        throw new RestServiceException(response.getStatusCode());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus.Series series = response.getStatusCode().series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
            || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}
