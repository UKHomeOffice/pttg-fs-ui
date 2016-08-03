package uk.gov.digital.ho.proving.financial.audit

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import spock.lang.Specification

import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.SEARCH

/**
 * @Author Home Office Digital
 */
class AuditActionsSpec extends Specification {

    def 'puts eventId in the data' (){

        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = AuditActions.auditEvent(SEARCH, eventId, new HashMap<String, Object>())

        then:
        e.auditEvent.data.get("eventId") == eventId
    }

    def 'creates data map if it is null' (){

        when:
        def eventId = AuditActions.nextId();
        AuditApplicationEvent e = AuditActions.auditEvent(SEARCH, eventId, null)

        then:
        e.auditEvent.data != null
    }

    def 'generates event ids' (){
        expect:
        AuditActions.nextId() != AuditActions.nextId()
    }
}
