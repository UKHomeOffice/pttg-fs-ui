package uk.gov.digital.ho.proving.financial.health.indicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import uk.gov.digital.ho.proving.financial.health.UrlConnectionTester;

/**
 * Gets the HTTP status of the API. Annotate this class with @Component to add this indicator to the health check
 * which is used for the kubernetes readiness probe.
 *
 *   @Author Home Office Digital
 */
public class ApiConnectionHealth implements HealthIndicator {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiConnectionHealth.class);

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.healthcheck.endpoint}")
    private String apiEndpoint;

    private UrlConnectionTester tester = new UrlConnectionTester();

    private int timeout = 1000;

    @Override
    public Health health() {

        int responseCode = tester.getResponseCodeFor(apiRoot + apiEndpoint, timeout);

        LOGGER.debug("API healthcheck response code: {}", responseCode);

        if (responseCode != 200) {
            return Health.down().withDetail("While trying to read financial status service, received:", responseCode).build();
        }

        return Health.up().withDetail("The financial status service API is responding with:", "UP").build();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


}
