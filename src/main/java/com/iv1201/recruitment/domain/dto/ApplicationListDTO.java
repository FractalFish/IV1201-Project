package com.iv1201.recruitment.domain.dto;

import com.iv1201.recruitment.domain.ApplicationStatus;
import java.time.LocalDateTime;

/**
 * DTO for displaying application summary in list views.
 */
public class ApplicationListDTO {

    private Integer applicationId;
    private String personName;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    public ApplicationListDTO() {}

    public ApplicationListDTO(Integer applicationId, String personName, 
                              ApplicationStatus status, LocalDateTime createdAt) {
        this.applicationId = applicationId;
        this.personName = personName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
