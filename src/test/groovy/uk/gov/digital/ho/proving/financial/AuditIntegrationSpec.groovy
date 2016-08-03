package uk.gov.digital.ho.proving.financial

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.financial.model.ResponseDetails

import static java.time.LocalDateTime.now
import static java.time.LocalDateTime.parse
import static java.time.temporal.ChronoUnit.MINUTES

/**
 * @Author Home Office Digital
 */
@SpringApplicationConfiguration(classes = ServiceRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(properties = [
    "api.root=http://localhost:8989"
])
class AuditIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    def port

    def path = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus?"
    def params = "toDate=2015-01-01&inLondon=true&studentType=nondoctorate&courseLength=1&totalTuitionFees=1&tuitionFeesAlreadyPaid=1&accommodationFeesAlreadyPaid=1&numberOfDependants=1"
    def url

    RestTemplate restTemplate
    WireMockTestDataLoader apiServerMock

    @Autowired
    AuditEventRepository auditEventRepository

    def thresholdUrlRegex = "/pttg/financialstatusservice/v1/maintenance/threshold*"
    def balanceCheckUrlRegex = "/pttg/financialstatusservice/v1/accounts.*"

    def logAppender = Mock(Appender.class)

    def setup() {
        restTemplate = new TestRestTemplate()
        url = "http://localhost:" + port + path + params

        apiServerMock = new WireMockTestDataLoader(8989)

        withMockLogAppender()
    }

    def withMockLogAppender() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logAppender);
    }

    def successResponses() {
        apiServerMock.stubTestData("threshold", thresholdUrlRegex)
        apiServerMock.stubTestData("dailyBalancePass", balanceCheckUrlRegex)
    }


    def "Searches are audited as INFO level log output with AUDIT prefix in json format and SEARCH type with a timestamp"() {

        given:
        successResponses()

        List<LoggingEvent> logEntries = []

        _ * logAppender.doAppend(_) >> { arg ->
            if (arg[0].formattedMessage.contains("AUDIT")) {
                logEntries.add(arg[0])
            }
        }

        when:
        restTemplate.getForEntity(url, ResponseDetails.class)
        LoggingEvent logEntry = logEntries[0]
        def logEntryJson = new JsonSlurper().parseText(logEntry.formattedMessage - "AUDIT:")

        then:

        logEntries.size >= 1
        logEntry.level == Level.INFO

        logEntryJson.principal == "anonymous"
        logEntryJson.type == "SEARCH"
        logEntryJson.data.method == "daily-balance-status"

        MINUTES.between(parse(logEntryJson.timestamp), now()) < 1;
    }


}
