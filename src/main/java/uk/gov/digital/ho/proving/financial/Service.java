package uk.gov.digital.ho.proving.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse;
import uk.gov.digital.ho.proving.financial.health.ApiAvailabilityChecker;
import uk.gov.digital.ho.proving.financial.integration.FinancialStatusChecker;
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

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Autowired
    private FinancialStatusChecker financialStatusChecker;

    @Autowired
    private ApiAvailabilityChecker apiAvailabilityChecker;


    @RequestMapping(path = "t4/accounts/{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @Valid Account account,
        @Valid Course course,
        @Valid Maintenance maintenance,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @CookieValue(value="kc-access", defaultValue = "") String accessToken
    ) {
        LOGGER.debug("Status for: account: {}, course: {}, maintenance: {}, toDate: {}, dependants: {}", account, course, maintenance, toDate);

        FundingCheckResponse result = financialStatusChecker.checkDailyBalanceStatus("t4", account, toDate, course, maintenance, accessToken);
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
        FundingCheckResponse result = financialStatusChecker.checkDailyBalanceStatus(tier, account, toDate, null, null, accessToken);
        return ResponseEntity.ok(result);
    }



    @RequestMapping(path = "availability", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity availability(){
        return apiAvailabilityChecker.check();
    }





}
