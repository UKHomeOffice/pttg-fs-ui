package uk.gov.digital.ho.proving.financial.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * @Author Home Office Digital
 */
@Component
public class JsonLoggingNonPersistentAuditEventRepository implements AuditEventRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(JsonLoggingNonPersistentAuditEventRepository.class);

    private static final String AUDIT_EVENT_LOG_MARKER = "AUDIT";

    private final ObjectMapper mapper;

    public JsonLoggingNonPersistentAuditEventRepository() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(INDENT_OUTPUT);
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
