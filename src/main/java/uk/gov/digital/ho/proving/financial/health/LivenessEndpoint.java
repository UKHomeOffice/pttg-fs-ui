package uk.gov.digital.ho.proving.financial.health;

/**
 * Created by Home Office Digital.
 */

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.stereotype.Component;

/**
 * Endpoint that can be used for liveness checks, but not for Readiness.
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
