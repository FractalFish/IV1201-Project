package com.iv1201.recruitment.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * JPA Entity representing a person's availability period in the recruitment system.
 * Maps to the 'availability' table in the database.
 * 
 * Represents the date range during which a person is available to work.
 * 
 * @author IV1201 Team
 */
@Entity
@Table(name = "availability")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Integer availabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    /**
     * Default constructor required by JPA.
     */
    public Availability() {
    }

    /**
     * Constructor with all required fields.
     * 
     * @param person The person who is available
     * @param fromDate Start date of availability period
     * @param toDate End date of availability period
     */
    public Availability(Person person, LocalDate fromDate, LocalDate toDate) {
        this.person = person;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    // Getters and Setters

    /**
     * Gets the availability ID.
     * 
     * @return the availability ID
     */
    public Integer getAvailabilityId() {
        return availabilityId;
    }

    /**
     * Sets the availability ID.
     * 
     * @param availabilityId the availability ID to set
     */
    public void setAvailabilityId(Integer availabilityId) {
        this.availabilityId = availabilityId;
    }

    /**
     * Gets the person associated with this availability period.
     * 
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the person associated with this availability period.
     * 
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Gets the start date of the availability period.
     * 
     * @return the from date
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * Sets the start date of the availability period.
     * 
     * @param fromDate the from date to set
     */
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the end date of the availability period.
     * 
     * @return the to date
     */
    public LocalDate getToDate() {
        return toDate;
    }

    /**
     * Sets the end date of the availability period.
     * 
     * @param toDate the to date to set
     */
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "availabilityId=" + availabilityId +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
