package uk.gov.digital.ho.proving.financial.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @Author Home Office Digital
 */
@Component
public class ApiConnectionHealth implements HealthIndicator {

    @Override
    public Health health() {

        int errorCode = testServerConnection();

        if (errorCode != 0) {
            return Health.down().withDetail("While trying to read financial status service, received:", errorCode).build();
        }

        return Health.up().withDetail("The financial status service API is responding with:","UP").build();
    }

    private int testServerConnection() {
        // to do - really test the server connection
        return new Random().nextInt(10) % 2;
    }

}
