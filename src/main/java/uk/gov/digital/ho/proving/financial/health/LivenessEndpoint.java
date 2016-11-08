package uk.gov.digital.ho.proving.financial.health;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.stereotype.Component;

/**
 * SpringBoot Endpoint that answers a liveness probe with success as long as the app has started and can serve this
 * response. Specify /ping as the livenessProbe:httpGet:path in the deployment yaml.
 *
 * Note that the readiness probe is answered by the Spring Boot healthcheck - implement a HealthIndicator to modify
 * the health check response.
 */
@Component
public class LivenessEndpoint extends AbstractEndpoint<String> {

    public LivenessEndpoint() {
        super("ping");
    }

    @Override
    public String invoke() {
        return "pong";
    }
}
