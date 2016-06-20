package uk.gov.digital.ho.proving.financial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Account implements Serializable {

    private final String sortCode;
    private final String accountNumber;

    @JsonCreator
    public Account(@JsonProperty("sortCode") String sortCode,
                   @JsonProperty("accountNumber") String accountNumber) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "Account{" +
            "sortCode='" + sortCode + '\'' +
            ", accountNumber='" + accountNumber + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(sortCode, account.sortCode) &&
            Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortCode, accountNumber);
    }
}
