package uk.gov.digital.ho.proving.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.financial.api.ConsentCheckResponse;
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse;
import uk.gov.digital.ho.proving.financial.api.ThresholdResponse;
import uk.gov.digital.ho.proving.financial.health.ApiAvailabilityChecker;
import uk.gov.digital.ho.proving.financial.integration.FinancialStatusChecker;
import uk.gov.digital.ho.proving.financial.integration.ThresholdResult;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.Course;
import uk.gov.digital.ho.proving.financial.model.Maintenance;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping(path = "/pttg/financialstatus/v1/")
@ControllerAdvice
public class Service {

    private final String TIER_4 = "t4";

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Autowired
    private FinancialStatusChecker financialStatusChecker;

    @Autowired
    private ApiAvailabilityChecker apiAvailabilityChecker;

    @RequestMapping(path = "t4/threshold", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity threshold(
        @Valid Course course,
        @Valid Maintenance maintenance,
        @RequestParam Boolean dependantsOnly,
        @CookieValue(value="kc-access", defaultValue = "") String accessToken
    ) {
        LOGGER.debug("Threshold for course: {}, maintenance: {}", course, maintenance);
        ThresholdResponse result = financialStatusChecker.checkThresholdTier4(course, maintenance, accessToken, dependantsOnly);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path = "{tier:t2|t5}/threshold", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity threshold(
        @PathVariable String tier,
        @RequestParam String applicantType,
        @RequestParam Integer dependants,
        @CookieValue(value="kc-access", defaultValue = "") String accessToken
    ) {
        LOGGER.debug("Threshold for applicantType: {}, dependants: {}", applicantType, dependants);
        ThresholdResponse result = financialStatusChecker.checkThreshold(tier, applicantType, dependants, accessToken);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path = "t4/accounts/{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @Valid Account account,
        @Valid Course course,
        @Valid Maintenance maintenance,
        @RequestParam Boolean dependantsOnly,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @CookieValue(value="kc-access", defaultValue = "") String accessToken
    ) {
        LOGGER.debug("Status for: account: {}, course: {}, maintenance: {}, toDate: {}, dependants: {}", account, course, maintenance, toDate);
        FundingCheckResponse result = financialStatusChecker.checkDailyBalanceStatusTier4(account, toDate, course, maintenance, accessToken, dependantsOnly);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path = "{tier:t2|t5}/accounts/{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @Valid Account account,
        @PathVariable String tier,
        @RequestParam String applicantType,
        @RequestParam Integer dependants,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @CookieValue(value="kc-access", defaultValue = "") String accessToken
    ) {
        LOGGER.debug("Status for: account: {}, applicantType: {}, dependants: {}", account, applicantType, dependants);
        FundingCheckResponse result = financialStatusChecker.checkDailyBalanceStatus(tier, account, toDate, applicantType, dependants, accessToken);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path = "accounts/{sortCode}/{accountNumber}/consent", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity consent(
        @Valid Account account,
        @CookieValue(value="kc-access", defaultValue = "") String accessToken
    ) {
        LOGGER.debug("Consent for account: {}", account);
        ConsentCheckResponse result = financialStatusChecker.checkConsent(account, accessToken);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path = "availability", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity availability(){
        return apiAvailabilityChecker.check();
    }

}
