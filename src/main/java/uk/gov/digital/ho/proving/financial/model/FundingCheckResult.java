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

    private final BigDecimal threshold;


    @JsonCreator
    public FundingCheckResult(@JsonProperty("sortCode") String sortCode,
                              @JsonProperty("accountNumber") String accountNumber,
                              @JsonProperty("fundingRequirementMet") boolean fundingRequirementMet,
                              @JsonProperty("periodCheckedFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedFrom,
                              @JsonProperty("periodCheckedTo")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedTo,
                              @JsonProperty("threshold") BigDecimal threshold){

        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.fundingRequirementMet = fundingRequirementMet;
        this.periodCheckedFrom = periodCheckedFrom;
        this.periodCheckedTo = periodCheckedTo;
        this.threshold = threshold;
    }

    public FundingCheckResult(DailyBalanceCheckResponse apiResult) {
        this.sortCode = apiResult.getAccount().getSortCode();
        this.accountNumber = apiResult.getAccount().getAccountNumber();
        this.fundingRequirementMet = apiResult.getDailyBalanceCheck().isMinimumAboveThreshold();
        this.periodCheckedFrom = apiResult.getDailyBalanceCheck().getAssessmentStartDate();
        this.periodCheckedTo = apiResult.getDailyBalanceCheck().getApplicationRaisedDate();
        this.threshold = apiResult.getDailyBalanceCheck().getThreshold();
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

    public BigDecimal getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "FundingCheckResult{" +
            "sortCode='" + sortCode + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            ", fundingRequirementMet=" + fundingRequirementMet +
            ", periodCheckedFrom=" + periodCheckedFrom +
            ", periodCheckedTo=" + periodCheckedTo +
            ", threshold=" + threshold +
            '}';
    }
}
