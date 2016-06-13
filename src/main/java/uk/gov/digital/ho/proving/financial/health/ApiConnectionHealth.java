package uk.gov.digital.ho.proving.financial.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author Home Office Digital
 */
@Component
public class ApiConnectionHealth implements HealthIndicator {

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.endpoint}")
    private String apiEndpoint;

    private UrlConnectionTester tester = new UrlConnectionTester();

    @Override
    public Health health() {

        // todo - connect to healthcheck uri on api server
        int errorCode = tester.getResponseCodeFor(apiRoot + apiEndpoint);

        if (errorCode == 0) {
            return Health.down().withDetail("While trying to read financial status service, received:", errorCode).build();
        }

        return Health.up().withDetail("The financial status service API is responding with:", "UP").build();
    }


    public static class UrlConnectionTester {

        public int getResponseCodeFor(String uri){

            try {
                URL url = new URL(uri);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();

                return connection.getResponseCode();

            } catch (Exception e) {
                return 0;
            }
        }
    }
}
