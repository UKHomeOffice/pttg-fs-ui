package uk.gov.digital.ho.proving.financial.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.digital.ho.proving.financial.api.FundingCheckResponse;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.Course;
import uk.gov.digital.ho.proving.financial.model.Maintenance;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpMethod.GET;

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

    private HttpEntity<?> entity = new HttpEntity<>(getHeaders());

    public FundingCheckResponse checkDailyBalanceStatus(Account account, LocalDate toDate, Course course, Maintenance maintenance) {

        BigDecimal totalFundsRequired = getThreshold(course, maintenance).getThreshold();
        DailyBalanceStatusResult dailyBalanceStatus = getDailyBalanceStatus(account, toDate, totalFundsRequired);

        return new FundingCheckResponse(dailyBalanceStatus);
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
        DailyBalanceStatusResult dailyBalanceStatusResult = getForObject(uri, DailyBalanceStatusResult.class);

        LOGGER.debug("Daily balance status result: {}", dailyBalanceStatusResult);

        return dailyBalanceStatusResult
            .withMinimum(totalFundsRequired)
            .withFromDate(fromDate);
    }

    private LocalDate daysBefore(LocalDate toDate) {
        return toDate.minusDays(daysToCheck - 1);
    }

    private <T> T getForObject(URI uri, Class<T> type) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(uri, GET, entity, type);
        return responseEntity.getBody();
    }

    private HttpHeaders getHeaders(){

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(asList(MediaType.APPLICATION_JSON));

        return headers;
    }

}
