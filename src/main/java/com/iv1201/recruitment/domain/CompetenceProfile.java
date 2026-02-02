package com.iv1201.recruitment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * JPA Entity representing a person's competence profile in the recruitment system.
 * This entity links a Person to a Competence with years of experience.
 * Maps to the 'competence_profile' table in the database.
 * 
 * Represents the many-to-many relationship between Person and Competence,
 * with additional attribute (years_of_experience).
 * 
 * @author IV1201 Team
 */
@Entity
@Table(name = "competence_profile")
public class CompetenceProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competence_profile_id")
    private Integer competenceProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competence_id")
    private Competence competence;

    @Column(name = "years_of_experience", precision = 4, scale = 2)
    private BigDecimal yearsOfExperience;

    /**
     * Default constructor required by JPA.
     */
    public CompetenceProfile() {
    }

    /**
     * Constructor with all required fields.
     * 
     * @param person The person who has this competence
     * @param competence The competence
     * @param yearsOfExperience Years of experience in this competence
     */
    public CompetenceProfile(Person person, Competence competence, BigDecimal yearsOfExperience) {
        this.person = person;
        this.competence = competence;
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getters and Setters

    /**
     * Gets the competence profile ID.
     * 
     * @return the competence profile ID
     */
    public Integer getCompetenceProfileId() {
        return competenceProfileId;
    }

    /**
     * Sets the competence profile ID.
     * 
     * @param competenceProfileId the competence profile ID to set
     */
    public void setCompetenceProfileId(Integer competenceProfileId) {
        this.competenceProfileId = competenceProfileId;
    }

    /**
     * Gets the person associated with this competence profile.
     * 
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the person associated with this competence profile.
     * 
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Gets the competence.
     * 
     * @return the competence
     */
    public Competence getCompetence() {
        return competence;
    }

    /**
     * Sets the competence.
     * 
     * @param competence the competence to set
     */
    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    /**
     * Gets the years of experience in this competence.
     * 
     * @return the years of experience
     */
    public BigDecimal getYearsOfExperience() {
        return yearsOfExperience;
    }

    /**
     * Sets the years of experience in this competence.
     * 
     * @param yearsOfExperience the years of experience to set
     */
    public void setYearsOfExperience(BigDecimal yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    @Override
    public String toString() {
        return "CompetenceProfile{" +
                "competenceProfileId=" + competenceProfileId +
                ", yearsOfExperience=" + yearsOfExperience +
                '}';
    }
}
