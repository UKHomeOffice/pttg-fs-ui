package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class DailyBalanceCheck implements Serializable {

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate applicationRaisedDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate assessmentStartDate;

    private final int threshold;
    private final boolean minimumAboveThreshold;

    @JsonCreator
    public DailyBalanceCheck(@JsonProperty("applicationRaisedDate") @DateTimeFormat(iso = ISO.DATE) LocalDate applicationRaisedDate,
                             @JsonProperty("assessmentStartDate") @DateTimeFormat(iso = ISO.DATE) LocalDate assessmentStartDate,
                             @JsonProperty("threshold") int threshold,
                             @JsonProperty("minimumAboveThreshold") boolean minimumAboveThreshold) {
        this.applicationRaisedDate = applicationRaisedDate;
        this.assessmentStartDate = assessmentStartDate;
        this.threshold = threshold;
        this.minimumAboveThreshold = minimumAboveThreshold;
    }

    public LocalDate getApplicationRaisedDate() {
        return applicationRaisedDate;
    }

    public LocalDate getAssessmentStartDate() {
        return assessmentStartDate;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean isMinimumAboveThreshold() {
        return minimumAboveThreshold;
    }

    @Override
    public String toString() {
        return "DailyBalanceCheck{" +
            "applicationRaisedDate=" + applicationRaisedDate +
            ", assessmentStartDate=" + assessmentStartDate +
            ", threshold=" + threshold +
            ", minimumAboveThreshold=" + minimumAboveThreshold +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyBalanceCheck that = (DailyBalanceCheck) o;
        return threshold == that.threshold &&
            minimumAboveThreshold == that.minimumAboveThreshold &&
            Objects.equals(applicationRaisedDate, that.applicationRaisedDate) &&
            Objects.equals(assessmentStartDate, that.assessmentStartDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationRaisedDate, assessmentStartDate, threshold, minimumAboveThreshold);
    }
}
