package com.iv1201.recruitment.domain.dto;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Form DTO for submitting a complete application with competences and availabilities.
 */
public class ApplicationFormDTO {

    @Valid
    private List<CompetenceForm> competences = new ArrayList<>();

    @Valid
    private List<AvailabilityForm> availabilities = new ArrayList<>();

    public ApplicationFormDTO() {}

    public List<CompetenceForm> getCompetences() { return competences; }
    public void setCompetences(List<CompetenceForm> competences) { this.competences = competences; }

    public List<AvailabilityForm> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<AvailabilityForm> availabilities) { this.availabilities = availabilities; }
}
