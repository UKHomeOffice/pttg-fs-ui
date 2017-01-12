package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CappedValues {

    private final BigDecimal accommodationFeesPaid;
    private final int courseLength;

    @JsonCreator
    public CappedValues(@JsonProperty("accommodationFeesPaid") BigDecimal accommodationFeesPaid,
                        @JsonProperty("courseLength") int courseLength
    ) {
        this.accommodationFeesPaid = accommodationFeesPaid;
        this.courseLength = courseLength;
    }

    public BigDecimal getAccommodationFeesPaid() {
        return accommodationFeesPaid;
    }

    public int getCourseLength() {
        return courseLength;
    }

    @Override
    public String toString() {
        return "CappedValues{" +
            "accommodationFeesPaid=" + accommodationFeesPaid +
            ", courseLength=" + courseLength +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CappedValues that = (CappedValues) o;

        if (courseLength != that.courseLength) return false;
        return accommodationFeesPaid != null ? accommodationFeesPaid.equals(that.accommodationFeesPaid) : that.accommodationFeesPaid == null;

    }

    @Override
    public int hashCode() {
        int result = accommodationFeesPaid != null ? accommodationFeesPaid.hashCode() : 0;
        result = 31 * result + courseLength;
        return result;
    }
}
