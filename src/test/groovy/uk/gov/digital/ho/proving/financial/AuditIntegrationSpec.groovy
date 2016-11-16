package uk.gov.digital.ho.proving.financial

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import static java.time.LocalDateTime.now
import static java.time.temporal.ChronoUnit.MINUTES
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @Author Home Office Digital
 */
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [ServiceRunner.class],
    properties = [
        "api.root=http://localhost:8989"
    ])
class AuditIntegrationSpec extends Specification {

    def path = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus?"
    def params = "dob=1990-10-04&toDate=2015-01-01&inLondon=true&studentType=nondoctorate&courseStartDate=2016-01-01&courseEndDate=2016-01-01&continuationEndDate&totalTuitionFees=1&tuitionFeesAlreadyPaid=1&accommodationFeesAlreadyPaid=1&numberOfDependants=1"
    def url

    @Autowired
    TestRestTemplate restTemplate
    WireMockTestDataLoader apiServerMock

    @Autowired
    AuditEventRepository auditEventRepository

    def thresholdUrlRegex = "/pttg/financialstatusservice/v1/maintenance/threshold*"
    def balanceCheckUrlRegex = "/pttg/financialstatusservice/v1/accounts.*"

    Appender logAppender = Mock()

    def setup() {
        url = path + params

        apiServerMock = new WireMockTestDataLoader(8989)

        withMockLogAppender()
    }

    def cleanup() {
        apiServerMock.stop()
    }

    def withMockLogAppender() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logAppender);
    }

    def successResponses() {
        apiServerMock.stubTestData("threshold", thresholdUrlRegex)
        apiServerMock.stubTestData("dailyBalancePass", balanceCheckUrlRegex)
    }


    def "Searches are audited as INFO level log output with AUDIT prefix and SEARCH type with a timestamp"() {

        given:

        List<LoggingEvent> logEntries = []

        _ * logAppender.doAppend(_) >> { arg ->

            if (arg[0].formattedMessage.contains("AUDIT")) {
                logEntries.add(arg[0])
            }
        }

        when:
        restTemplate.getForEntity(url, ResponseDetails.class)
        LoggingEvent logEntry = logEntries[0]

        then:

        logEntry.level == Level.INFO

        // We can capture the SEARCH event log even though the search fails because there is no mongo

        logEntry.formattedMessage.contains("principal=anonymous")
        logEntry.formattedMessage.contains("type=SEARCH")
        logEntry.formattedMessage.contains("method=daily-balance-status")

        LocalDateTime timestamp =
            Instant.ofEpochMilli(logEntry.timeStamp).atZone(ZoneId.systemDefault()).toLocalDateTime();

        MINUTES.between(timestamp, now()) < 1;
    }


}
