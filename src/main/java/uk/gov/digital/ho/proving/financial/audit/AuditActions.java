package uk.gov.digital.ho.proving.financial.audit;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Home Office Digital
 */
public class AuditActions {

    public static UUID nextId() {
        return UUID.randomUUID();
    }

    public static AuditApplicationEvent auditEvent(AuditEventType type, UUID id, Map<String, Object> data) {

        if (data == null) data = new HashMap<String, Object>();

        data.put("eventId", id);

        return new AuditApplicationEvent(getPrincipal(), type.name(), data);
    }

    private static String getPrincipal() {
        return "anonymous";
    }
}
