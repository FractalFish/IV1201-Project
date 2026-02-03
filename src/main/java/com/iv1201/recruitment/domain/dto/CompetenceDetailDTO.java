package com.iv1201.recruitment.domain.dto;

import java.math.BigDecimal;

/**
 * DTO for displaying competence details in an application.
 */
public class CompetenceDetailDTO {

    private String competenceName;
    private BigDecimal yearsOfExperience;

    public CompetenceDetailDTO() {}

    public CompetenceDetailDTO(String competenceName, BigDecimal yearsOfExperience) {
        this.competenceName = competenceName;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getCompetenceName() { return competenceName; }
    public void setCompetenceName(String competenceName) { this.competenceName = competenceName; }

    public BigDecimal getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(BigDecimal yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
}
