package uk.gov.digital.ho.proving.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse;
import uk.gov.digital.ho.proving.financial.integration.FinancialStatusChecker;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.Course;
import uk.gov.digital.ho.proving.financial.model.Maintenance;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author Home Office Digital
 */
@RestController
@RequestMapping(path = "/pttg/financialstatusservice/v1/accounts/")
@ControllerAdvice
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Autowired
    private FinancialStatusChecker financialStatusChecker;

    @RequestMapping(path = "{sortCode}/{accountNumber}/dailybalancestatus", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity status(
        @Valid Account account,
        @Valid Course course,
        @Valid Maintenance maintenance,
        @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestHeader HttpHeaders headers
    ) {
        LOGGER.debug("Status for: account: {}, course: {}, maintenance: {}, toDate: {}, dependants: {}", account, course, maintenance, toDate);

        List<String> authHeader = headers.get("kc-access");
        String accessToken  = (authHeader != null && authHeader.size()> 0) ? authHeader.get(0) : "";

        FundingCheckResponse result = financialStatusChecker.checkDailyBalanceStatus(account, toDate, course, maintenance, accessToken);
        return ResponseEntity.ok(result);
    }

}
