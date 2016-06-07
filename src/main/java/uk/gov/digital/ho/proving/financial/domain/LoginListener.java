package uk.gov.digital.ho.proving.financial.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Author Home Office Digital
 */
@Component
public class LoginListener implements ApplicationListener<AuditApplicationEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginListener.class);

    @Override
    public void onApplicationEvent(AuditApplicationEvent event) {

        LOGGER.debug("audit event: {}", event.getAuditEvent().toString());
    }
}
