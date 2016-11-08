package uk.gov.digital.ho.proving.financial.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Created by Home Office Digital.
 */
@Component
public class ApiAvailabilityChecker {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiAvailabilityChecker.class);

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.availability.endpoint}")
    private String apiEndpoint;

    private UrlConnectionTester tester = new UrlConnectionTester();

    private int timeout = 1000;

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public ResponseEntity check() {

        int responseCode = tester.getResponseCodeFor(apiRoot + apiEndpoint, timeout);

        LOGGER.debug("API availability response code: {}", responseCode);

        if (responseCode != 200) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
