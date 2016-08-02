package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CappedValues {

    private final String accommodationFeesPaid;
    private final int courseLength;

    @JsonCreator
    public CappedValues(@JsonProperty("accommodationFeesPaid") String accommodationFeesPaid,
                        @JsonProperty("courseLength") int courseLength) {
        this.accommodationFeesPaid = accommodationFeesPaid;
        this.courseLength = courseLength;
    }

    public String getAccommodationFeesPaid() {
        return accommodationFeesPaid;
    }

    public int getCourseLength() {
        return courseLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CappedValues that = (CappedValues) o;
        return courseLength == that.courseLength &&
            Objects.equals(accommodationFeesPaid, that.accommodationFeesPaid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accommodationFeesPaid, courseLength);
    }

    @Override
    public String toString() {
        return "CappedValues{" +
            "accommodationFeesPaid='" + accommodationFeesPaid + '\'' +
            ", courseLength=" + courseLength +
            '}';
    }
}
