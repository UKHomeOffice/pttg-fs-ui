package uk.gov.digital.ho.proving.financial.integration

import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financial.api.ConsentCheckResponse
import uk.gov.digital.ho.proving.financial.api.ConsentStatus
import uk.gov.digital.ho.proving.financial.audit.AuditEventType
import uk.gov.digital.ho.proving.financial.model.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static java.math.BigDecimal.ONE
import static java.time.temporal.ChronoUnit.DAYS
import static org.springframework.http.HttpStatus.OK

/**
 * @Author Home Office Digital
 */
class FinancialStatusCheckerSpec extends Specification {

    public static final String ACCOUNT_HOLDER_NAME = "Ray Purchase"
    FinancialStatusChecker checker

    String tier = "t4";
    LocalDate dob = LocalDate.of(1980,1,1);
    Account account = new Account("", "", dob)
    LocalDate toDate = LocalDate.now()
    LocalDate aDate = LocalDate.of(2016, 1, 1)
    Course course = new Course(true, aDate, aDate, "nondoctorate", "main")
    Maintenance maintenance = new Maintenance(ONE, ONE, ONE, 1)

    def recordCountFailure = new FailureReason(27)

    def thresholdResponse = thresholdOf(ONE)
    def dailyBalanceResponse = new ResponseEntity(new DailyBalanceStatusResult(ACCOUNT_HOLDER_NAME, true, recordCountFailure, new ResponseDetails("", "")), OK)

    ApiUrls urls = Mock()
    RestTemplate template = Mock()
    ApplicationEventPublisher auditor = Mock()

    def setup() {
        checker = new FinancialStatusChecker()

        checker.restTemplate = template
        checker.apiUrls = urls
        checker.auditor = auditor
    }

    def thresholdOf(BigDecimal minimum) {
        def threshold = BigDecimal.valueOf(minimum)
        def thresholdResult = new ThresholdResult(threshold, LocalDate.of(2000,1,1), new CappedValues(100, 9), new ResponseDetails("200", "OK"))
        thresholdResponse = new ResponseEntity(thresholdResult, OK);
    }

    def 'delegates construction of api service urls'() {

        given:
        template.exchange(_, _, _, ThresholdResult.class) >> thresholdResponse
        template.exchange(_, _, _, DailyBalanceStatusResult.class) >> dailyBalanceResponse

        when:
        checker.checkDailyBalanceStatusTier4(account, toDate, course, maintenance, "token", true)

        then:
        1 * urls.t4ThresholdUrlFor(*_)
        1 * urls.dailyBalanceStatusUrlFor(*_)
    }

    def 'uses result of threshold for daily balance status call'() {

        given:
        thresholdOf(123.45)
        template.exchange(_, _, _, ThresholdResult.class) >> thresholdResponse
        template.exchange(_, _, _, DailyBalanceStatusResult.class) >> dailyBalanceResponse

        when:
        checker.checkDailyBalanceStatusTier4(account, toDate, course, maintenance, "token", true)

        then:
        1 * urls.dailyBalanceStatusUrlFor(_, 123.45, _, _)
    }

    @Unroll("Date range is inclusive, so when period is #period days, then there are #between days between from and to")
    def 'calculates fromDate so that fromDate to toDate inclusive equals daysToCheck'() {

        given:
        checker.daysToCheckT4 = period
        template.exchange(_, _, _, ThresholdResult.class) >> thresholdResponse
        template.exchange(_, _, _, DailyBalanceStatusResult.class) >> dailyBalanceResponse

        when:
        checker.checkDailyBalanceStatusTier4(account, toDate, course, maintenance, "token", true)

        then:
        1 * urls.dailyBalanceStatusUrlFor(_, _, { DAYS.between(it, toDate) == between }, toDate)

        where:
        period | between
        28     | 27
        10     | 9
    }

    def 'adds calculated threshold and fromDate to outgoing result'() {

        given:
        checker.daysToCheckT4 = 10
        thresholdOf(123.45)
        template.exchange(_, _, _, ThresholdResult.class) >> thresholdResponse
        template.exchange(_, _, _, DailyBalanceStatusResult.class) >> dailyBalanceResponse

        when:
        def response = checker.checkDailyBalanceStatusTier4(account, toDate, course, maintenance, "token", true)

        then:
        response.minimum == 123.45
        response.periodCheckedFrom == toDate.minusDays(9)
    }

    def 'audits search inputs and response'() {

        given:
        template.exchange(_, _, _, ThresholdResult.class) >> thresholdResponse
        template.exchange(_, _, _, DailyBalanceStatusResult.class) >> dailyBalanceResponse

        AuditEvent event1
        AuditEvent event2
        1 * auditor.publishEvent(_) >> {args -> event1 = args[0].auditEvent}
        1 * auditor.publishEvent(_) >> {args -> event2 = args[0].auditEvent}

        when:
        checker.checkDailyBalanceStatusTier4(account, toDate, course, maintenance, "token", true)

        then:

        event1.type == AuditEventType.SEARCH.name()
        event2.type == AuditEventType.SEARCH_RESULT.name()

        event1.data['eventId'] == event2.data['eventId']

        event1.data['account'] == account
        event1.data['toDate'] == toDate.format(DateTimeFormatter.ISO_DATE)
        event1.data['course'] == course
        event1.data['maintenance'] == maintenance

        event2.data['response'].failureReason == recordCountFailure
    }

    def 'passes through the details retrieved from the API when checking consent'() {

        given:
        def account = new Account("12-34-56", "123456789", LocalDate.of(1984, 1, 1))
        def stubbedResponseBody = new ConsentCheckResponse("SUCCESS", new ConsentStatus("200", "OK"))
        def stubbedConsentCheckerResponseEntity = new ResponseEntity<>(stubbedResponseBody, OK)
        template.exchange(_, _, _, ConsentCheckResponse.class) >> stubbedConsentCheckerResponseEntity

        when:
        ConsentCheckResponse response = checker.checkConsent(account, "token")

        then:
        response == stubbedResponseBody

    }
}
