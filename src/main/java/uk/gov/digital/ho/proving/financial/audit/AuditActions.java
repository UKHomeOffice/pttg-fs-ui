package uk.gov.digital.ho.proving.financial.audit;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import java.rmi.server.UID;
import java.util.Map;
import java.util.UUID;

import uk.gov.digital.ho.proving.financial.audit.AuditEventType;

/**
 * @Author Home Office Digital
 */
public class AuditActions {

    public static UUID nextId(){
        return UUID.randomUUID();
    }

    public static AuditApplicationEvent auditEvent(AuditEventType type, String... data) {
        AuditEvent e = new AuditEvent(getPrincipal(), type.name(), data);
        return new AuditApplicationEvent(e);
    }

    public static AuditApplicationEvent auditEvent(AuditEventType type, Map<String, Object> data) {
        AuditEvent e = new AuditEvent(getPrincipal(), type.name(), data);
        return new AuditApplicationEvent(e);
    }

    private static String getPrincipal() {
        return "anonymous";
    }
}
