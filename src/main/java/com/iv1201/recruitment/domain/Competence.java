package com.iv1201.recruitment.domain;

import jakarta.persistence.*;

/**
 * JPA Entity representing a competence (skill/expertise area) in the recruitment system.
 * Maps to the 'competence' table in the database.
 * 
 * Examples of competences: ticket sales, lotteries, roller coaster operation, etc.
 * 
 * @author IV1201 Team
 */
@Entity
@Table(name = "competence")
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competence_id")
    private Integer competenceId;

    @Column(name = "name", length = 255)
    private String name;

    /**
     * Default constructor required by JPA.
     */
    public Competence() {
    }

    /**
     * Constructor with name parameter.
     * 
     * @param name The name of the competence
     */
    public Competence(String name) {
        this.name = name;
    }

    // Getters and Setters

    /**
     * Gets the competence ID.
     * 
     * @return the competence ID
     */
    public Integer getCompetenceId() {
        return competenceId;
    }

    /**
     * Sets the competence ID.
     * 
     * @param competenceId the competence ID to set
     */
    public void setCompetenceId(Integer competenceId) {
        this.competenceId = competenceId;
    }

    /**
     * Gets the name of the competence.
     * 
     * @return the competence name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the competence.
     * 
     * @param name the competence name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Competence{" +
                "competenceId=" + competenceId +
                ", name='" + name + '\'' +
                '}';
    }
}
