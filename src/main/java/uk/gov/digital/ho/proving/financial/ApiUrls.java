package uk.gov.digital.ho.proving.financial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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


    public URI thresholdUrlFor(int courseLength, int totalTuitionFees, int tuitionFeesAlreadyPaid, int accommodationFeesAlreadypaid) {
        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiThresholdEndpoint)
            .queryParam("courseLength", courseLength)
            .queryParam("totalTuitionFees", totalTuitionFees)
            .queryParam("tuitionFeesAlreadyPaid", tuitionFeesAlreadyPaid)
            .queryParam("accommodationFeesAlreadypaid", accommodationFeesAlreadypaid)
            .build()
            .toUri();

        LOGGER.debug(expanded.toString());

        return expanded;
    }

    public URI dailyBalanceStatusUrlFor(String accountNumber, String sortCode, BigDecimal totalFundsRequired, LocalDate from, LocalDate to) {

        URI expanded = UriComponentsBuilder.fromUriString(apiRoot + apiDailyBalanceEndpoint)
            .queryParam("minimum", totalFundsRequired.toPlainString())
            .queryParam("fromDate", from)
            .queryParam("toDate", to)
            .buildAndExpand(sortCode, accountNumber)
            .toUri();

        LOGGER.debug(expanded.toString());

        return expanded;
    }
}
