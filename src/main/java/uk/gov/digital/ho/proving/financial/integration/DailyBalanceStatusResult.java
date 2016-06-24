package uk.gov.digital.ho.proving.financial.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.digital.ho.proving.financial.model.Account;
import uk.gov.digital.ho.proving.financial.model.ResponseDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DailyBalanceStatusResult implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Account account;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate fromDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate toDate;

    private final BigDecimal minimum;

    private final boolean pass;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ResponseDetails status;

    @JsonCreator
    public DailyBalanceStatusResult(@JsonProperty("account") Account account,
                                    @JsonProperty("fromDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                    @JsonProperty("toDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                    @JsonProperty("minimum") BigDecimal minimum,
                                    @JsonProperty("pass") boolean pass,
                                    @JsonProperty("status") ResponseDetails status) {
        this.account = account;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.minimum = minimum;
        this.pass = pass;
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }


    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public boolean isPass() {
        return pass;
    }

    public ResponseDetails getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return "DailyBalanceStatusResult{" +
            "account=" + account +
            ", fromDate=" + fromDate +
            ", toDate=" + toDate +
            ", minimum=" + minimum +
            ", pass=" + pass +
            ", status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyBalanceStatusResult that = (DailyBalanceStatusResult) o;
        return pass == that.pass &&
            Objects.equals(account, that.account) &&
            Objects.equals(fromDate, that.fromDate) &&
            Objects.equals(toDate, that.toDate) &&
            Objects.equals(minimum, that.minimum) &&
            Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, fromDate, toDate, minimum, pass, status);
    }
}
