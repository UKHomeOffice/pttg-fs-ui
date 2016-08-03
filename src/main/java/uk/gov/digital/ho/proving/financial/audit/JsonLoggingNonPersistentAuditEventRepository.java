package uk.gov.digital.ho.proving.financial.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author Home Office Digital
 */
@Component
public class JsonLoggingNonPersistentAuditEventRepository implements AuditEventRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(JsonLoggingNonPersistentAuditEventRepository.class);

    private static final String AUDIT_EVENT_LOG_MARKER = "AUDIT";

    private ObjectMapper mapper = new ObjectMapper();

    public JsonLoggingNonPersistentAuditEventRepository() {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        //mapper.disable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public List<AuditEvent> find(String principal, Date after) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void add(AuditEvent event) {
        LOGGER.info("{}: {}", AUDIT_EVENT_LOG_MARKER, jsonOf(event));
    }

    private String jsonOf(AuditEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return event.toString();
        }
    }
}
