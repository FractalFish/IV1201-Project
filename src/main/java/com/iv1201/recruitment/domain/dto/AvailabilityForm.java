package com.iv1201.recruitment.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Form DTO for submitting an availability period.
 */
public class AvailabilityForm {

    @NotNull(message = "{validation.fromDate.required}")
    private LocalDate fromDate;

    @NotNull(message = "{validation.toDate.required}")
    private LocalDate toDate;

    public AvailabilityForm() {}

    public AvailabilityForm(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    /**
     * Validates that toDate is not before fromDate.
     * @return true if dates are valid
     */
    public boolean isValid() {
        return fromDate != null && toDate != null && !toDate.isBefore(fromDate);
    }
}
