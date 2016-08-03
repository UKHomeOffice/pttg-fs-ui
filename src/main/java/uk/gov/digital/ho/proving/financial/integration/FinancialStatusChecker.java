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

    @Value("${daily-balance.days-to-check}")
    private int daysToCheck;

    @Autowired
    private ApiUrls apiUrls;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationEventPublisher auditor;

    private HttpEntity<?> entity = new HttpEntity<>(getHeaders());

    @Retryable(interceptor = "connectionExceptionInterceptor")
    public FundingCheckResponse checkDailyBalanceStatus(Account account, LocalDate toDate, Course course, Maintenance maintenance) {

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(account, toDate, course, maintenance)));

        ThresholdResult thresholdResult = getThreshold(course, maintenance);
        DailyBalanceStatusResult dailyBalanceStatus = getDailyBalanceStatus(account, toDate, thresholdResult.getThreshold());

        FundingCheckResponse fundingCheckResponse = new FundingCheckResponse(dailyBalanceStatus, thresholdResult);

        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(fundingCheckResponse)));

        return fundingCheckResponse;
    }

    private ThresholdResult getThreshold(Course course, Maintenance maintenance) {
        URI uri = apiUrls.thresholdUrlFor(course, maintenance);
        ThresholdResult thresholdResult = getForObject(uri, ThresholdResult.class);

        LOGGER.debug("Threshold result: {}", thresholdResult);

        return thresholdResult;
    }

    private DailyBalanceStatusResult getDailyBalanceStatus(Account account, LocalDate toDate, BigDecimal totalFundsRequired) {

        LocalDate fromDate = daysBefore(toDate);

        URI uri = apiUrls.dailyBalanceStatusUrlFor(account, totalFundsRequired, fromDate, toDate);

        DailyBalanceStatusResult dailyBalanceStatusResult =
            getForObject(uri, DailyBalanceStatusResult.class)
                .withFromDate(fromDate);

        LOGGER.debug("Daily balance status result: {}", dailyBalanceStatusResult);

        return dailyBalanceStatusResult;
    }

    private LocalDate daysBefore(LocalDate toDate) {
        return toDate.minusDays(daysToCheck - 1);
    }


    private <T> T getForObject(URI uri, Class<T> type) {
        LOGGER.debug("Calling API with URI: {} for type: {}", uri, type);
        ResponseEntity<T> responseEntity = restTemplate.exchange(uri, GET, entity, type);
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
}
