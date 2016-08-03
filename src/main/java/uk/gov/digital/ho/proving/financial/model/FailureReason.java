package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class FailureReason {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer recordCount;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDate lowestBalanceDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final BigDecimal lowestBalanceValue;

    @JsonCreator
    public FailureReason(@JsonProperty("recordCount") Integer recordCount,
                         @JsonProperty("lowestBalanceDate") LocalDate lowestBalanceDate,
                         @JsonProperty("lowestBalanceValue") BigDecimal lowestBalanceValue) {
        this.recordCount = recordCount;
        this.lowestBalanceDate = lowestBalanceDate;
        this.lowestBalanceValue = lowestBalanceValue;
    }

    public FailureReason(Integer recordCount) {
        this.recordCount = recordCount;
        this.lowestBalanceDate = null;
        this.lowestBalanceValue = null;
    }

    public FailureReason(LocalDate lowestBalanceDate, BigDecimal lowestBalanceValue) {
        this.recordCount = null;
        this.lowestBalanceDate = lowestBalanceDate;
        this.lowestBalanceValue = lowestBalanceValue;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public LocalDate getLowestBalanceDate() {
        return lowestBalanceDate;
    }

    public BigDecimal getLowestBalanceValue() {
        return lowestBalanceValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureReason that = (FailureReason) o;
        return Objects.equals(recordCount, that.recordCount) &&
            Objects.equals(lowestBalanceDate, that.lowestBalanceDate) &&
            Objects.equals(lowestBalanceValue, that.lowestBalanceValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordCount, lowestBalanceDate, lowestBalanceValue);
    }

    @Override
    public String toString() {
        return "FailureReason{" +
            "recordCount=" + recordCount +
            ", lowestBalanceDate=" + lowestBalanceDate +
            ", lowestBalanceValue=" + lowestBalanceValue +
            '}';
    }
}
