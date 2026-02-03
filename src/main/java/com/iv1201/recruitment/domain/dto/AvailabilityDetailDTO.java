package com.iv1201.recruitment.domain.dto;

import java.time.LocalDate;

/**
 * DTO for displaying availability details in an application.
 */
public class AvailabilityDetailDTO {

    private LocalDate fromDate;
    private LocalDate toDate;

    public AvailabilityDetailDTO() {}

    public AvailabilityDetailDTO(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
}
