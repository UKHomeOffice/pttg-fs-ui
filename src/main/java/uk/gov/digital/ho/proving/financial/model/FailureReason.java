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
    private final LocalDate dateFundsNotMet; //minimumBalanceDate

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final BigDecimal amount; //minimumBalanceValue

    @JsonCreator
    public FailureReason(@JsonProperty("recordCount") Integer recordCount,
                         @JsonProperty("dateFundsNotMet") LocalDate dateFundsNotMet,
                         @JsonProperty("amount") BigDecimal amount) {
        this.recordCount = recordCount;
        this.dateFundsNotMet = dateFundsNotMet;
        this.amount = amount;
    }

    public FailureReason(Integer recordCount) {
        this.recordCount = recordCount;
        this.dateFundsNotMet = null;
        this.amount = null;
    }

    public FailureReason(LocalDate dateFundsNotMet, BigDecimal amount) {
        this.recordCount = null;
        this.dateFundsNotMet = dateFundsNotMet;
        this.amount = amount;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public LocalDate getDateFundsNotMet() {
        return dateFundsNotMet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureReason that = (FailureReason) o;
        return Objects.equals(recordCount, that.recordCount) &&
            Objects.equals(dateFundsNotMet, that.dateFundsNotMet) &&
            Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordCount, dateFundsNotMet, amount);
    }

    @Override
    public String toString() {
        return "FailureReason{" +
            "recordCount=" + recordCount +
            ", dateFundsNotMet=" + dateFundsNotMet +
            ", amount=" + amount +
            '}';
    }
}
