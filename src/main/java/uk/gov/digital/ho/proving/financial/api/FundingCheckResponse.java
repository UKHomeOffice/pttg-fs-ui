package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.digital.ho.proving.financial.integration.DailyBalanceStatusResult;

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

    @JsonCreator
    public FundingCheckResponse(@JsonProperty("fundingRequirementMet") boolean fundingRequirementMet,
                                @JsonProperty("periodCheckedFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodCheckedFrom,
                                @JsonProperty("minimum") BigDecimal minimum) {

        this.fundingRequirementMet = fundingRequirementMet;
        this.periodCheckedFrom = periodCheckedFrom;
        this.minimum = minimum;
    }

    public FundingCheckResponse(DailyBalanceStatusResult result) {
        this.fundingRequirementMet = result.isPass();
        this.periodCheckedFrom = result.getFromDate();
        this.minimum = result.getMinimum();
    }

    public boolean getFundingRequirementMet() {
        return fundingRequirementMet;
    }

    public LocalDate getPeriodCheckedFrom() {
        return periodCheckedFrom;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    @Override
    public String toString() {
        return "FundingCheckResponse{" +
            "fundingRequirementMet=" + fundingRequirementMet +
            ", periodCheckedFrom=" + periodCheckedFrom +
            ", minimum=" + minimum +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundingCheckResponse that = (FundingCheckResponse) o;
        return fundingRequirementMet == that.fundingRequirementMet &&
            Objects.equals(periodCheckedFrom, that.periodCheckedFrom) &&
            Objects.equals(minimum, that.minimum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundingRequirementMet, periodCheckedFrom, minimum);
    }
}
