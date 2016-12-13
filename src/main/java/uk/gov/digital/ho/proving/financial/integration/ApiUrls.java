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

    @Value("${api.threshold.endpoint}")
    private String apiThresholdEndpoint;


    public URI thresholdUrlFor(Course course, Maintenance maintenance) {

        LOGGER.debug("root: {}, end: {}", apiRoot, apiDailyBalanceEndpoint);

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiThresholdEndpoint)
            .queryParam("inLondon", course.getInLondon())
            .queryParam("studentType", course.getStudentType())
            .queryParam("courseStartDate", course.getCourseStartDate())
            .queryParam("courseEndDate", course.getCourseEndDate())
            .queryParam("originalCourseStartDate", course.getOriginalCourseStartDate())
            .queryParam("tuitionFees", maintenance.getTotalTuitionFees())
            .queryParam("tuitionFeesPaid", maintenance.getTuitionFeesAlreadyPaid())
            .queryParam("accommodationFeesPaid", maintenance.getAccommodationFeesAlreadyPaid())
            .queryParam("dependants", maintenance.getNumberOfDependants())
            .queryParam("courseType", course.getCourseType())
            .build()
            .toUri();

        LOGGER.debug(expanded.toString());

        return expanded;
    }

    public URI dailyBalanceStatusUrlFor(Account account, BigDecimal totalFundsRequired, LocalDate from, LocalDate to) {

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiDailyBalanceEndpoint)
            .queryParam("dob", account.getDob())
            //TODO replace when user and consent values are captured
            .queryParam("userId", "anonymous")
            .queryParam("accountHolderConsent", Boolean.TRUE)
            .queryParam("minimum", totalFundsRequired.toPlainString())
            .queryParam("fromDate", from)
            .queryParam("toDate", to)
            .buildAndExpand(account.getSortCode(), account.getAccountNumber())
            .toUri();

        LOGGER.debug(expanded.toString());

        return expanded;
    }
}
