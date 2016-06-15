package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyBalanceStatusResponse implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account account;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DailyBalanceCheck dailyBalanceCheck;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ResponseStatus status;

    @JsonCreator
    public DailyBalanceStatusResponse(@JsonProperty("account") Account account,
                                      @JsonProperty("dailyBalanceCheck") DailyBalanceCheck dailyBalanceCheck,
                                      @JsonProperty("status") ResponseStatus status) {
        this.account = account;
        this.dailyBalanceCheck = dailyBalanceCheck;
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public DailyBalanceCheck getDailyBalanceCheck() {
        return dailyBalanceCheck;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "DailyBalanceStatusResponse{" +
            "account=" + account +
            ", dailyBalanceCheck=" + dailyBalanceCheck +
            ", status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyBalanceStatusResponse that = (DailyBalanceStatusResponse) o;
        return Objects.equals(account, that.account) &&
            Objects.equals(dailyBalanceCheck, that.dailyBalanceCheck) &&
            Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, dailyBalanceCheck, status);
    }
}
