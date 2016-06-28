package uk.gov.digital.ho.proving.financial.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ThresholdResult implements Serializable {

    private final BigDecimal threshold;

    @JsonCreator
    public ThresholdResult(@JsonProperty("threshold") BigDecimal threshold){
        this.threshold = threshold;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "ThresholdResult{" +
            "threshold=" + threshold +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThresholdResult that = (ThresholdResult) o;
        return Objects.equals(threshold, that.threshold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threshold);
    }
}
