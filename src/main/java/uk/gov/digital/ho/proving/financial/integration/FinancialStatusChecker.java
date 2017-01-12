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

    @Retryable(interceptor = "connectionExceptionInterceptor")
    public FundingCheckResponse checkDailyBalanceStatus(String tier, Account account, LocalDate toDate, Course course, Maintenance maintenance, String accessToken) {

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(account, toDate, course, maintenance)));
        LOGGER.debug("checkDailyBalanceStatus search - account: {}, LocalDate: {}, Course: {}, Maintenance: {}", account, toDate, course, maintenance);

        ThresholdResult thresholdResult = getThreshold(tier, course, maintenance, accessToken);
        DailyBalanceStatusResult dailyBalanceStatus = getDailyBalanceStatus(tier, account, toDate, thresholdResult.getThreshold(), accessToken);

        FundingCheckResponse fundingCheckResponse = new FundingCheckResponse(dailyBalanceStatus, thresholdResult);

        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(fundingCheckResponse)));

        return fundingCheckResponse;
    }

    private ThresholdResult getThreshold(String tier, Course course, Maintenance maintenance, String accessToken) {

        ThresholdResult thresholdResult = null;

        switch (tier.toLowerCase()) {
            case "t4":
                URI t4Uri = apiUrls.t4ThresholdUrlFor(course, maintenance);
                thresholdResult = getForObject(t4Uri, ThresholdResult.class, accessToken);
                break;
            case "t2":
                URI t2Uri = apiUrls.t5ThresholdUrlFor(course, "xxx", -1);
                thresholdResult = getForObject(t2Uri, ThresholdResult.class, accessToken);
                break;

            case "t5":
                URI t5Uri = apiUrls.t5ThresholdUrlFor(course, "xxx", -1);
                thresholdResult = getForObject(t5Uri, ThresholdResult.class, accessToken);
                break;
        }

        LOGGER.debug("Threshold result: {}", value("thresholdResult", thresholdResult));
        return thresholdResult;
    }

    private DailyBalanceStatusResult getDailyBalanceStatus(String tier, Account account, LocalDate toDate, BigDecimal totalFundsRequired, String accessToken) {

        LocalDate fromDate = daysBefore(toDate, tier);

        DailyBalanceStatusResult dailyBalanceStatusResult = null;

        switch (tier.toLowerCase()) {
            case "t4":
                URI t4Uri = apiUrls.t4DailyBalanceStatusUrlFor(account, totalFundsRequired, fromDate, toDate);
                dailyBalanceStatusResult = getForObject(t4Uri, DailyBalanceStatusResult.class, accessToken)
                    .withFromDate(fromDate);
                break;

            case "t2":
                URI t2Uri = apiUrls.t2DailyBalanceStatusUrlFor(account, totalFundsRequired, fromDate, toDate);
                dailyBalanceStatusResult = getForObject(t2Uri, DailyBalanceStatusResult.class, accessToken)
                    .withFromDate(fromDate);
                break;

            case "t5":
                URI t5Uri = apiUrls.t5DailyBalanceStatusUrlFor(account, totalFundsRequired, fromDate, toDate);
                dailyBalanceStatusResult = getForObject(t5Uri, DailyBalanceStatusResult.class, accessToken)
                    .withFromDate(fromDate);
                break;
        }

        LOGGER.debug("Daily balance status result: {}", value("dailyBalanceStatusResult", dailyBalanceStatusResult));
        return dailyBalanceStatusResult;
    }

    private LocalDate daysBefore(LocalDate toDate, String tier) {

        LocalDate fromDate = null;

        switch (tier) {
            case "t2":
                fromDate = toDate.minusDays(daysToCheckT2T5 - 1);
                break;
            case "t4":
                fromDate = toDate.minusDays(daysToCheckT4 - 1);
                break;
            case "t5":
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

    private Map<String, Object> auditData(Account account, LocalDate toDate, Course course, Maintenance maintenance) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "daily-balance-status");
        auditData.put("account", account);
        auditData.put("toDate", toDate.format(DateTimeFormatter.ISO_DATE));
        auditData.put("course", course);
        auditData.put("maintenance", maintenance);

        return auditData;
    }

    private Map<String, Object> auditData(FundingCheckResponse response) {

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
