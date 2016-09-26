package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.digital.ho.proving.financial.integration.DailyBalanceStatusResult;
import uk.gov.digital.ho.proving.financial.integration.ThresholdResult;
import uk.gov.digital.ho.proving.financial.model.CappedValues;
import uk.gov.digital.ho.proving.financial.model.FailureReason;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class FundingCheckResponse implements Serializable {

    private final boolean fundingRequirementMet;

    private final String accountHolderName;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodCheckedFrom;

    private final BigDecimal minimum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final FailureReason failureReason;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final CappedValues cappedValues;

    @JsonCreator
    public FundingCheckResponse(@JsonProperty("fundingRequirementMet") boolean fundingRequirementMet,
                                @JsonProperty("accountHolderName") String accountHolderName,
                                @JsonProperty("periodCheckedFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedFrom,
                                @JsonProperty("minimum") BigDecimal minimum,
                                @JsonProperty("failureReason") FailureReason failureReason,
                                @JsonProperty("cappedValues") CappedValues cappedValues
    ) {
        this.accountHolderName = accountHolderName;
        this.fundingRequirementMet = fundingRequirementMet;
        this.periodCheckedFrom = periodCheckedFrom;
        this.minimum = minimum;
        this.failureReason = failureReason;
        this.cappedValues = cappedValues;
    }

    public FundingCheckResponse(DailyBalanceStatusResult balanceStatus, ThresholdResult threshold) {

        this.accountHolderName = balanceStatus.getAccountHolderName();
        this.fundingRequirementMet = balanceStatus.isPass();
        this.periodCheckedFrom = balanceStatus.getFromDate();
        this.failureReason = balanceStatus.getFailureReason();

        this.cappedValues = threshold.getCappedValues();
        this.minimum = threshold.getThreshold();
    }

    public boolean getFundingRequirementMet() {
        return fundingRequirementMet;
    }

    public boolean isFundingRequirementMet() {
        return fundingRequirementMet;
    }

    public LocalDate getPeriodCheckedFrom() {
        return periodCheckedFrom;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public CappedValues getCappedValues() {
        return cappedValues;
    }

    public FailureReason getFailureReason() {
        return failureReason;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundingCheckResponse that = (FundingCheckResponse) o;
        return fundingRequirementMet == that.fundingRequirementMet &&
            Objects.equals(periodCheckedFrom, that.periodCheckedFrom) &&
            Objects.equals(minimum, that.minimum) &&
            Objects.equals(accountHolderName, that.accountHolderName) &&
            Objects.equals(failureReason, that.failureReason) &&
            Objects.equals(cappedValues, that.cappedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundingRequirementMet, periodCheckedFrom, minimum, failureReason, cappedValues, accountHolderName);
    }

    @Override
    public String toString() {
        return "FundingCheckResponse{" +
            "fundingRequirementMet=" + fundingRequirementMet +
            ", periodCheckedFrom=" + periodCheckedFrom +
            ", minimum=" + minimum +
            ", accountHolderName=" + accountHolderName +
            ", failureReason=" + failureReason +
            ", cappedValues=" + cappedValues +
            '}';
    }
}
