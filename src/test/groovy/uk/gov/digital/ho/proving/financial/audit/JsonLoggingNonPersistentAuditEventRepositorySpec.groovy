package uk.gov.digital.ho.proving.financial.audit

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.boot.test.TestRestTemplate
import spock.lang.Specification
import steps.WireMockTestDataLoader

/**
 * @Author Home Office Digital
 */
class JsonLoggingNonPersistentAuditEventRepositorySpec extends Specification {

    def repo
    Appender logAppender = Mock()

    def setup() {

        repo = new JsonLoggingNonPersistentAuditEventRepository();

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logAppender);
    }

    def 'gives empty list for find'(){

        given:
        def event = new AuditEvent("principal", "audit-type", "audit-data=test")
        repo.add(event)

        expect:
        repo.find("principal", event.getTimestamp().minus(1)).isEmpty()
    }

    def 'sends audit events to logger'(){

        given:
        AuditEvent event = new AuditEvent("audit-type", "audit-data=test")

        when:
        repo.add(event)

        then:
        1 * logAppender.doAppend(_) >> { arg ->

            arg[0].level == Level.INFO
            arg[0].formattedMessage.contains("\"type\" : \"audit-type\"")
            arg[0].formattedMessage.contains("\"audit-data\" : \"test\"")
        }

    }
}
