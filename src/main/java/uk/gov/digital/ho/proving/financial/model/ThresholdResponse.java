package uk.gov.digital.ho.proving.financial.model;

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
public final class ThresholdResponse implements Serializable {

    private final BigDecimal threshold;

    @JsonCreator
    public ThresholdResponse(@JsonProperty("threshold") BigDecimal threshold){
        this.threshold = threshold;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "ThresholdResponse{" +
            "threshold='" + threshold + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThresholdResponse that = (ThresholdResponse) o;
        return Objects.equals(threshold, that.threshold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threshold);
    }
}
