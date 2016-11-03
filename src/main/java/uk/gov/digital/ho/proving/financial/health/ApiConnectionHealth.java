package uk.gov.digital.ho.proving.financial.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.proving.financial.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author Home Office Digital
 */
@Component
public class ApiConnectionHealth implements HealthIndicator {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiConnectionHealth.class);

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.healthcheck.endpoint}")
    private String apiEndpoint;

    private UrlConnectionTester tester = new UrlConnectionTester();

    @Override
    public Health health() {

        int responseCode = tester.getResponseCodeFor(apiRoot + apiEndpoint);

        LOGGER.debug("API healthcheck response code: {}", responseCode);

        if (responseCode != 200) {
            return Health.down().withDetail("While trying to read financial status service, received:", responseCode).build();
        }

        return Health.up().withDetail("The financial status service API is responding with:", "UP").build();
    }


    public static class UrlConnectionTester {

        public int getResponseCodeFor(String uri){

            try {

                LOGGER.debug("Checking API health using uri: {}", uri);
                URL url = new URL(uri);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                return connection.getResponseCode();

            } catch (Exception e) {
                LOGGER.warn("Exception while checking API health: {}", e.getMessage());
                LOGGER.warn("Exception", e);
                return 0;
            }
        }
    }
}
