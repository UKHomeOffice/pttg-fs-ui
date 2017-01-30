package uk.gov.digital.ho.proving.financial.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.Course;
import uk.gov.digital.ho.proving.financial.model.Maintenance;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@Component
public class ApiUrls {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiUrls.class);

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.dailybalance.endpoint}")
    private String apiDailyBalanceEndpoint;

    @Value("${api.consent.endpoint}")
    private String apiConsentEndpoint;

    @Value("${api.threshold.t4.endpoint}")
    private String apiThresholdT4Endpoint;

    @Value("${api.threshold.t2.endpoint}")
    private String apiThresholdT2Endpoint;

    @Value("${api.threshold.t5.endpoint}")
    private String apiThresholdT5Endpoint;


    public URI t4ThresholdUrlFor(Course course, Maintenance maintenance) {

        LOGGER.debug("root: {}, end: {}", apiRoot, apiThresholdT4Endpoint);

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiThresholdT4Endpoint)
            .queryParam("inLondon", course.getInLondon())
            .queryParam("studentType", course.getStudentType())
            .queryParam("courseStartDate", course.getCourseStartDate())
            .queryParam("courseEndDate", course.getCourseEndDate())
            .queryParam("originalCourseStartDate", course.getOriginalCourseStartDate())
            .queryParam("tuitionFees", maintenance.getTotalTuitionFees())
            .queryParam("tuitionFeesPaid", maintenance.getTuitionFeesAlreadyPaid())
            .queryParam("accommodationFeesPaid", maintenance.getAccommodationFeesAlreadyPaid())
            .queryParam("dependants", maintenance.getDependants())
            .queryParam("courseType", course.getCourseType())
            .build()
            .toUri();

        LOGGER.debug(expanded.toString());

        return expanded;
    }

    public URI t2ThresholdUrlFor(String applicantType, Integer dependants) {

        LOGGER.debug("root: {}, end: {}", apiRoot, apiThresholdT2Endpoint);

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiThresholdT2Endpoint)
            .queryParam("applicantType", applicantType)
            .queryParam("dependants", dependants)
            .build()
            .toUri();

        LOGGER.debug(expanded.toString());
        return expanded;
    }

    public URI t5ThresholdUrlFor(String applicantType, Integer dependants) {

        LOGGER.debug("root: {}, end: {}", apiRoot, apiThresholdT5Endpoint);

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiThresholdT5Endpoint)
            .queryParam("applicantType", applicantType)
            .queryParam("dependants", dependants)
            .build()
            .toUri();

        LOGGER.debug(expanded.toString());
        return expanded;
    }

    public URI dailyBalanceStatusUrlFor(Account account, BigDecimal totalFundsRequired, LocalDate from, LocalDate to) {

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiDailyBalanceEndpoint)
            .queryParam("dob", account.getDob())
            .queryParam("minimum", totalFundsRequired.toPlainString())
            .queryParam("fromDate", from)
            .queryParam("toDate", to)
            .buildAndExpand(account.getSortCode(), account.getAccountNumber())
            .toUri();

        LOGGER.debug(expanded.toString());

        return expanded;
    }

    public URI consentUrlFor(Account account) {
        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiConsentEndpoint)
            .queryParam("dob", account.getDob())
            .buildAndExpand(account.getSortCode(), account.getAccountNumber())
            .toUri();
        LOGGER.debug(expanded.toString());
        return expanded;
    }
}
