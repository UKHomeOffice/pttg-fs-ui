package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundingCheckResult implements Serializable {

    private final String sortCode;
    private final String accountNumber;

    private final boolean fundingRequirementMet;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodCheckedFrom;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodCheckedTo;

    private final BigDecimal minimum;


    @JsonCreator
    public FundingCheckResult(@JsonProperty("sortCode") String sortCode,
                              @JsonProperty("accountNumber") String accountNumber,
                              @JsonProperty("fundingRequirementMet") boolean fundingRequirementMet,
                              @JsonProperty("periodCheckedFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedFrom,
                              @JsonProperty("periodCheckedTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedTo,
                              @JsonProperty("minimum") BigDecimal minimum) {

        this.sortCode = formatSortCode(sortCode);
        this.accountNumber = accountNumber;
        this.fundingRequirementMet = fundingRequirementMet;
        this.periodCheckedFrom = periodCheckedFrom;
        this.periodCheckedTo = periodCheckedTo;
        this.minimum = minimum;
    }

    public FundingCheckResult(DailyBalanceStatusResponse apiResult) {
        this.sortCode = formatSortCode(apiResult.getAccount().getSortCode());
        this.accountNumber = apiResult.getAccount().getAccountNumber();
        this.fundingRequirementMet = apiResult.getDailyBalanceCheck().isMinimumAboveThreshold();
        this.periodCheckedFrom = apiResult.getDailyBalanceCheck().getAssessmentStartDate();
        this.periodCheckedTo = apiResult.getDailyBalanceCheck().getApplicationRaisedDate();
        this.minimum = apiResult.getDailyBalanceCheck().getThreshold();
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isFundingRequirementMet() {
        return fundingRequirementMet;
    }

    public LocalDate getPeriodCheckedFrom() {
        return periodCheckedFrom;
    }

    public LocalDate getPeriodCheckedTo() {
        return periodCheckedTo;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    @Override
    public String toString() {
        return "FundingCheckResult{" +
            "sortCode='" + sortCode + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            ", fundingRequirementMet=" + fundingRequirementMet +
            ", periodCheckedFrom=" + periodCheckedFrom +
            ", periodCheckedTo=" + periodCheckedTo +
            ", minimum=" + minimum +
            '}';
    }

    private String formatSortCode(String sortCode) {

        if (sortCode.length() != 6) {
            return sortCode;
        }

        StringBuilder sb = new StringBuilder(sortCode);
        sb.insert(2,'-').insert(5,'-');

        return sb.toString();
    }
}
