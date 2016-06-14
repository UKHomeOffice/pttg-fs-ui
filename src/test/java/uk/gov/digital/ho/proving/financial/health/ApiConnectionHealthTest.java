package uk.gov.digital.ho.proving.financial.health;

import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author Home Office Digital
 */
public class ApiConnectionHealthTest {


    @Test
    public void ShouldReportDownWhenServerUnreachable() {

        ApiConnectionHealth healthCheck = new ApiConnectionHealth();

        ReflectionTestUtils.setField(healthCheck, "apiRoot", "");
        ReflectionTestUtils.setField(healthCheck, "apiEndpoint", "");

        Health result = healthCheck.health();

        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    public void ShouldReportUpWhenServerResponds() {

        ApiConnectionHealth healthCheck = new ApiConnectionHealth();

        ApiConnectionHealth.UrlConnectionTester returnsOkTester = new ApiConnectionHealth.UrlConnectionTester() {
            public int getResponseCodeFor(String uri) {
                return 200;
            }
        };

        ReflectionTestUtils.setField(healthCheck, "tester", returnsOkTester);

        Health result = healthCheck.health();

        assertThat(result.getStatus()).isEqualTo(Status.UP);
    }
}
