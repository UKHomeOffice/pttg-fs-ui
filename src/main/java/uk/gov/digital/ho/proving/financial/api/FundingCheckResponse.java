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

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodCheckedFrom;

    private final BigDecimal minimum;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDate minimumBalanceDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final BigDecimal minimumBalanceValue;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final CappedValues cappedValues;

    @JsonCreator
    public FundingCheckResponse(@JsonProperty("fundingRequirementMet") boolean fundingRequirementMet,
                                @JsonProperty("periodCheckedFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedFrom,
                                @JsonProperty("minimum") BigDecimal minimum,
                                @JsonProperty("minimumBalanceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minimumBalanceDate,
                                @JsonProperty("minimumBalanceValue") BigDecimal minimumBalanceValue,
                                @JsonProperty("cappedValues") CappedValues cappedValues
    ) {
        this.fundingRequirementMet = fundingRequirementMet;
        this.periodCheckedFrom = periodCheckedFrom;
        this.minimum = minimum;
        this.minimumBalanceDate = minimumBalanceDate;
        this.minimumBalanceValue = minimumBalanceValue;
        this.cappedValues = cappedValues;
    }

    public FundingCheckResponse(DailyBalanceStatusResult balanceStatus, ThresholdResult threshold) {

        this.fundingRequirementMet = balanceStatus.isPass();
        this.periodCheckedFrom = balanceStatus.getFromDate();
        this.minimumBalanceDate = balanceStatus.getDateFundsNotMet();
        this.minimumBalanceValue = balanceStatus.getAmount();

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

    public LocalDate getMinimumBalanceDate() {
        return minimumBalanceDate;
    }

    public BigDecimal getMinimumBalanceValue() {
        return minimumBalanceValue;
    }

    public CappedValues getCappedValues() {
        return cappedValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundingCheckResponse that = (FundingCheckResponse) o;
        return fundingRequirementMet == that.fundingRequirementMet &&
            Objects.equals(periodCheckedFrom, that.periodCheckedFrom) &&
            Objects.equals(minimum, that.minimum) &&
            Objects.equals(minimumBalanceDate, that.minimumBalanceDate) &&
            Objects.equals(minimumBalanceValue, that.minimumBalanceValue) &&
            Objects.equals(cappedValues, that.cappedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundingRequirementMet, periodCheckedFrom, minimum, minimumBalanceDate, minimumBalanceValue, cappedValues);
    }

    @Override
    public String toString() {
        return "FundingCheckResponse{" +
            "fundingRequirementMet=" + fundingRequirementMet +
            ", periodCheckedFrom=" + periodCheckedFrom +
            ", minimum=" + minimum +
            ", minimumBalanceDate=" + minimumBalanceDate +
            ", minimumBalanceValue=" + minimumBalanceValue +
            ", cappedValues=" + cappedValues +
            '}';
    }
}
