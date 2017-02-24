package uk.gov.digital.ho.proving.financial.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.financial.api.ConditionCodesResponse;
import uk.gov.digital.ho.proving.financial.audit.AuditActions;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpMethod.GET;
import static uk.gov.digital.ho.proving.financial.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.CONDITION_CODES;
import static uk.gov.digital.ho.proving.financial.audit.AuditEventType.CONDITION_CODES_RESULT;

@Component
public class ConditionCodesRequestor {

    private static Logger LOGGER = LoggerFactory.getLogger(ConditionCodesRequestor.class);

    @Autowired
    private ApiUrls apiUrls;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationEventPublisher auditor;

    private HttpEntity<?> entity = new HttpEntity<>(createHeaders());

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Retryable(interceptor = "connectionExceptionInterceptor")
    public ConditionCodesResponse retrieveConditionCodes(String studentType,
                                                         Boolean dependantsOnly,
                                                         Integer dependants,
                                                         Optional<LocalDate> courseStartDate,
                                                         Optional<LocalDate> courseEndDate,
                                                         Optional<String> courseType,
                                                         Optional<Boolean> recognisedBodyOrHEI,
                                                         String accessToken) {
        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(CONDITION_CODES, eventId,
            getConditionCodesAuditData(studentType, dependantsOnly, dependants, courseStartDate, courseEndDate,
                courseType, recognisedBodyOrHEI)));
        LOGGER.debug("retrieveConditionCodes - studentType: {}, dependantsOnly: {}, dependants: {}, courseStartDate: {}, courseEndDate: {}, courseType: {}, recognisedBodyOrHEI: {}",
            studentType, dependantsOnly, dependants, courseStartDate, courseEndDate, courseType, recognisedBodyOrHEI);

        URI uri = apiUrls.conditionCodesUrlFor(studentType, dependantsOnly, dependants, courseStartDate, courseEndDate, courseType, recognisedBodyOrHEI);
        ConditionCodesResponse response = getForObject(uri, ConditionCodesResponse.class, accessToken);
        auditor.publishEvent(auditEvent(CONDITION_CODES_RESULT, eventId, conditionCodesResponseAuditData(response)));
        return response;
    }

    private Map<String, Object> getConditionCodesAuditData(String studentType,
                                       Boolean dependantsOnly,
                                       Integer dependants,
                                       Optional<LocalDate> courseStartDate,
                                       Optional<LocalDate> courseEndDate,
                                       Optional<String> courseType,
                                       Optional<Boolean> recognisedBodyOrHEI) {
        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "retrieve-condition-codes");
        auditData.put("studentType", studentType);
        auditData.put("dependantsOnly", dependantsOnly);
        auditData.put("dependants", dependants);
        auditData.put("courseStartDate", courseStartDate);
        auditData.put("courseEndDate", courseEndDate);
        auditData.put("courseType", courseType);
        auditData.put("recognisedBodyOrHEI", recognisedBodyOrHEI);

        return auditData;
    }

    private Map<String, Object> conditionCodesResponseAuditData(ConditionCodesResponse response) {
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("response", "response");
        return auditData;
    }


    private HttpHeaders createHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(asList(MediaType.APPLICATION_JSON));

        return headers;
    }

    // FIXME: Code below duplicated from FinancialStatusChecker since this project is being replaced

    private <T> T getForObject(URI uri, Class<T> type, String accessToken) {
        LOGGER.debug("Calling API with URI: {} for type: {}", uri, type);
        ResponseEntity<T> responseEntity = restTemplate.exchange(uri, GET, addTokenToHeaders(entity, accessToken), type);
        return responseEntity.getBody();
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
