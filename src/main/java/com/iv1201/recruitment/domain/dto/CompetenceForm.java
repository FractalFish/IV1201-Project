package com.iv1201.recruitment.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * Form DTO for submitting a competence with years of experience.
 */
public class CompetenceForm {

    @NotNull(message = "{validation.competence.required}")
    private Integer competenceId;

    @NotNull(message = "{validation.years.required}")
    @DecimalMin(value = "0.0", message = "{validation.years.positive}")
    private BigDecimal yearsOfExperience;

    public CompetenceForm() {}

    public CompetenceForm(Integer competenceId, BigDecimal yearsOfExperience) {
        this.competenceId = competenceId;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Integer getCompetenceId() { return competenceId; }
    public void setCompetenceId(Integer competenceId) { this.competenceId = competenceId; }

    public BigDecimal getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(BigDecimal yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
}
