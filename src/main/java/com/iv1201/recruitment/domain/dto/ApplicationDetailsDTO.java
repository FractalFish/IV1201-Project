package com.iv1201.recruitment.domain.dto;

import com.iv1201.recruitment.domain.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for displaying full application details including competences and availabilities.
 */
public class ApplicationDetailsDTO {

    private Integer applicationId;
    private String personName;
    private String personEmail;
    private String personPnr;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
    private List<CompetenceDetailDTO> competences;
    private List<AvailabilityDetailDTO> availabilities;

    public ApplicationDetailsDTO() {}

    // Getters and Setters
    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public String getPersonEmail() { return personEmail; }
    public void setPersonEmail(String personEmail) { this.personEmail = personEmail; }

    public String getPersonPnr() { return personPnr; }
    public void setPersonPnr(String personPnr) { this.personPnr = personPnr; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public List<CompetenceDetailDTO> getCompetences() { return competences; }
    public void setCompetences(List<CompetenceDetailDTO> competences) { this.competences = competences; }

    public List<AvailabilityDetailDTO> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<AvailabilityDetailDTO> availabilities) { this.availabilities = availabilities; }
}
