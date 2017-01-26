package uk.gov.digital.ho.proving.financial.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.financial.api.ConsentCheckResponse;
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse;
import uk.gov.digital.ho.proving.financial.audit.AuditActions;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.Course;
import uk.gov.digital.ho.proving.financial.model.Maintenance;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.HttpMethod.GET;
import static uk.gov.digital.ho.proving.financial.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.CONSENT;
import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.CONSENT_RESULT;
import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.SEARCH_RESULT;

/**
 * @Author Home Office Digital
 */
@Component
public class FinancialStatusChecker {

    private static Logger LOGGER = LoggerFactory.getLogger(FinancialStatusChecker.class);

    @Value("${daily-balance.days-to-check.t4}")
    private int daysToCheckT4;

    @Value("${daily-balance.days-to-check.t2t5}")
    private int daysToCheckT2T5;

    @Autowired
    private ApiUrls apiUrls;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationEventPublisher auditor;

    private HttpEntity<?> entity = new HttpEntity<>(getHeaders());


    private final String TIER_2 = "t2";
    private final String TIER_4 = "t4";
    private final String TIER_5 = "t5";

    @Retryable(interceptor = "connectionExceptionInterceptor")
    public FundingCheckResponse checkDailyBalanceStatus(String tier, Account account, LocalDate toDate, Course course, Maintenance maintenance, String accessToken) {

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, dailyBalanceAuditData(account, toDate, course, maintenance)));
        LOGGER.debug("checkDailyBalanceStatus search - account: {}, LocalDate: {}, Course: {}, Maintenance: {}", account, toDate, course, maintenance);

        ThresholdResult thresholdResult = getThreshold(course, maintenance, accessToken);
        DailyBalanceStatusResult dailyBalanceStatus = getDailyBalanceStatus(tier, account, toDate, thresholdResult.getThreshold(), accessToken);

        FundingCheckResponse fundingCheckResponse = new FundingCheckResponse(dailyBalanceStatus, thresholdResult);

        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, dailyBalanceAuditData(fundingCheckResponse)));

        return fundingCheckResponse;
    }

    @Retryable(interceptor = "connectionExceptionInterceptor")
    public FundingCheckResponse checkDailyBalanceStatus(String tier, Account account, LocalDate toDate, String applicantType, Integer dependants, String accessToken) {

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, dailyBalanceAuditData(account, toDate, applicantType, dependants)));
        LOGGER.debug("checkDailyBalanceStatus search - account: {}, LocalDate: {}, String: {}, Integer: {}", account, toDate, applicantType, dependants);

        ThresholdResult thresholdResult = getThreshold(tier, applicantType, dependants, accessToken);
        DailyBalanceStatusResult dailyBalanceStatus = getDailyBalanceStatus(tier, account, toDate, thresholdResult.getThreshold(), accessToken);

        FundingCheckResponse fundingCheckResponse = new FundingCheckResponse(dailyBalanceStatus, thresholdResult);

        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, dailyBalanceAuditData(fundingCheckResponse)));

        return fundingCheckResponse;
    }

    @Retryable(interceptor = "connectionExceptionInterceptor")
    public ConsentCheckResponse checkConsent(Account account, String accessToken) {
        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(CONSENT, eventId, consentAuditData(account)));
        LOGGER.debug("checkConsent - account: {}", account);

        ConsentCheckResponse consentResult = getConsent(account, accessToken);
        Map<String, Object> responseAuditData = consentAuditData(consentResult);
        auditor.publishEvent(auditEvent(CONSENT_RESULT, eventId, responseAuditData));
        return consentResult;
    }

    private Map<String, Object> consentAuditData(ConsentCheckResponse consentResult) {
        Map<String, Object> responseAuditData = new HashMap<>();

        responseAuditData.put("method", "check-consent");
        responseAuditData.put("response", consentResult);
        return responseAuditData;
    }

    private Map<String, Object> consentAuditData(Account account) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("method", "check-consent");
        auditData.put("account", account);
        return auditData;
    }

    private ConsentCheckResponse getConsent(Account account, String accessToken) {
        URI consentUri = apiUrls.consentUrlFor(account);
        ConsentCheckResponse consentCheckResult = getForObject(consentUri, ConsentCheckResponse.class, accessToken);
        LOGGER.debug("Consent result: {}", value("consentResult", consentCheckResult));
        return consentCheckResult;
    }


    private ThresholdResult getThreshold(String tier, String applicantType, Integer dependants, String accessToken) {

        ThresholdResult thresholdResult = null;

        switch (tier.toLowerCase()) {
            case TIER_2:
                URI t2Uri = apiUrls.t2ThresholdUrlFor(applicantType, dependants);
                thresholdResult = getForObject(t2Uri, ThresholdResult.class, accessToken);
                break;

            case TIER_5:
                URI t5Uri = apiUrls.t5ThresholdUrlFor(applicantType, dependants);
                thresholdResult = getForObject(t5Uri, ThresholdResult.class, accessToken);
                break;
        }

        LOGGER.debug("Threshold result: {}", value("thresholdResult", thresholdResult));
        return thresholdResult;
    }

    private ThresholdResult getThreshold(Course course, Maintenance maintenance, String accessToken) {

        URI t4Uri = apiUrls.t4ThresholdUrlFor(course, maintenance);
        ThresholdResult thresholdResult = getForObject(t4Uri, ThresholdResult.class, accessToken);

        LOGGER.debug("Threshold result: {}", value("thresholdResult", thresholdResult));
        return thresholdResult;
    }

    private DailyBalanceStatusResult getDailyBalanceStatus(String tier, Account account, LocalDate toDate, BigDecimal totalFundsRequired, String accessToken) {

        LocalDate fromDate = daysBefore(toDate, tier);

        URI uri = apiUrls.dailyBalanceStatusUrlFor(account, totalFundsRequired, fromDate, toDate);
        DailyBalanceStatusResult dailyBalanceStatusResult = getForObject(uri, DailyBalanceStatusResult.class, accessToken)
            .withFromDate(fromDate);

        LOGGER.debug("Daily balance status result: {}", value("dailyBalanceStatusResult", dailyBalanceStatusResult));
        return dailyBalanceStatusResult;
    }

    private LocalDate daysBefore(LocalDate toDate, String tier) {

        LocalDate fromDate = null;

        switch (tier) {
            case TIER_2:
                fromDate = toDate.minusDays(daysToCheckT2T5 - 1);
                break;
            case TIER_4:
                fromDate = toDate.minusDays(daysToCheckT4 - 1);
                break;
            case TIER_5:
                fromDate = toDate.minusDays(daysToCheckT2T5 - 1);
                break;
        }
        return fromDate;
    }


    private <T> T getForObject(URI uri, Class<T> type, String accessToken) {
        LOGGER.debug("Calling API with URI: {} for type: {}", uri, type);
        ResponseEntity<T> responseEntity = restTemplate.exchange(uri, GET, addTokenToHeaders(entity, accessToken), type);
        return responseEntity.getBody();
    }

    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(asList(MediaType.APPLICATION_JSON));

        return headers;
    }

    private Map<String, Object> dailyBalanceAuditData(Account account, LocalDate toDate, Course course, Maintenance maintenance) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "daily-balance-status");
        auditData.put("account", account);
        auditData.put("toDate", toDate.format(DateTimeFormatter.ISO_DATE));
        auditData.put("course", course);
        auditData.put("maintenance", maintenance);

        return auditData;
    }

    private Map<String, Object> dailyBalanceAuditData(Account account, LocalDate toDate, String applicantType, Integer dependants) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "daily-balance-status");
        auditData.put("account", account);
        auditData.put("toDate", toDate.format(DateTimeFormatter.ISO_DATE));
        auditData.put("applicantType", applicantType);
        auditData.put("dependants", dependants);

        return auditData;
    }


    private Map<String, Object> dailyBalanceAuditData(FundingCheckResponse response) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "daily-balance-status");
        auditData.put("response", response);

        return auditData;
    }

    private HttpEntity addTokenToHeaders(HttpEntity<?> entity, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(entity.getHeaders());
        headers.add("Cookie", "kc-access=" + accessToken);
        HttpEntity<?> newEntity = new HttpEntity<>(headers);

        LOGGER.debug("Request headers: " + newEntity.toString());
        LOGGER.debug("      kc-access: " + accessToken.toString());

        return newEntity;
    }
}
