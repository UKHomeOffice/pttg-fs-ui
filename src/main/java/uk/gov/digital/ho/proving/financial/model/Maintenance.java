package uk.gov.digital.ho.proving.financial.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
public final class Maintenance {

    private BigDecimal totalTuitionFees;
    private BigDecimal tuitionFeesAlreadyPaid;

    @NotNull(message = "Missing parameter")
    private Integer dependants;

    @NotNull(message = "Missing parameter")
    private BigDecimal accommodationFeesAlreadyPaid;

    public Maintenance() {

    }

    public Maintenance(BigDecimal totalTuitionFees, BigDecimal tuitionFeesAlreadyPaid, BigDecimal accommodationFeesAlreadyPaid, Integer dependants) {
        this.totalTuitionFees = totalTuitionFees;
        this.tuitionFeesAlreadyPaid = tuitionFeesAlreadyPaid;
        this.accommodationFeesAlreadyPaid = accommodationFeesAlreadyPaid;
        this.dependants = dependants;
    }

    public BigDecimal getTotalTuitionFees() {
        return totalTuitionFees;
    }

    public void setTotalTuitionFees(BigDecimal totalTuitionFees) {
        this.totalTuitionFees = totalTuitionFees;
    }

    public BigDecimal getTuitionFeesAlreadyPaid() {
        return tuitionFeesAlreadyPaid;
    }

    public void setTuitionFeesAlreadyPaid(BigDecimal tuitionFeesAlreadyPaid) {
        this.tuitionFeesAlreadyPaid = tuitionFeesAlreadyPaid;
    }

    public BigDecimal getAccommodationFeesAlreadyPaid() {
        return accommodationFeesAlreadyPaid;
    }

    public void setAccommodationFeesAlreadyPaid(BigDecimal accommodationFeesAlreadyPaid) {
        this.accommodationFeesAlreadyPaid = accommodationFeesAlreadyPaid;
    }

    public Integer getDependants() {
        return dependants;
    }

    public void setDependants(Integer dependants) {
        this.dependants = dependants;
    }

    @Override
    public String toString() {
        return "Maintenance{" +
            "totalTuitionFees=" + totalTuitionFees +
            ", tuitionFeesAlreadyPaid=" + tuitionFeesAlreadyPaid +
            ", dependants=" + dependants +
            ", accommodationFeesAlreadyPaid=" + accommodationFeesAlreadyPaid +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return Objects.equals(totalTuitionFees, that.totalTuitionFees) &&
            Objects.equals(tuitionFeesAlreadyPaid, that.tuitionFeesAlreadyPaid) &&
            Objects.equals(dependants, that.dependants) &&
            Objects.equals(accommodationFeesAlreadyPaid, that.accommodationFeesAlreadyPaid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalTuitionFees, tuitionFeesAlreadyPaid, dependants, accommodationFeesAlreadyPaid);
    }
}
