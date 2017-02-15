package uk.gov.digital.ho.proving.financial.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import uk.gov.digital.ho.proving.financial.integration.ThresholdResult;
import uk.gov.digital.ho.proving.financial.model.CappedValues;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThresholdResponse implements Serializable {

    private static final long serialVersionUID = 4143856345496276199L;

    private final BigDecimal threshold;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate leaveEndDate;
    private final CappedValues cappedValues;

    public ThresholdResponse(ThresholdResult thresholdResult) {
        threshold = thresholdResult.getThreshold();
        leaveEndDate = thresholdResult.getLeaveEndDate();
        cappedValues = thresholdResult.getCappedValues();
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public LocalDate getLeaveEndDate() {
        return leaveEndDate;
    }

    public CappedValues getCappedValues() {
        return cappedValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThresholdResponse that = (ThresholdResponse) o;

        if (threshold != null ? !threshold.equals(that.threshold) : that.threshold != null) return false;
        if (leaveEndDate != null ? !leaveEndDate.equals(that.leaveEndDate) : that.leaveEndDate != null) return false;
        return cappedValues != null ? cappedValues.equals(that.cappedValues) : that.cappedValues == null;
    }

    @Override
    public int hashCode() {
        int result = threshold != null ? threshold.hashCode() : 0;
        result = 31 * result + (leaveEndDate != null ? leaveEndDate.hashCode() : 0);
        result = 31 * result + (cappedValues != null ? cappedValues.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ThresholdResponse{" +
            "threshold=" + threshold +
            ", leaveEndDate=" + leaveEndDate +
            ", cappedValues=" + cappedValues +
            '}';
    }


}
