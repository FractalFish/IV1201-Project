package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Availability entities.
 * Provides CRUD operations and custom queries for availability period data.
 * 
 * @author IV1201 Team
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
    
    /**
     * Finds all availability periods for a specific person.
     * 
     * @param personId the ID of the person
     * @return list of availability periods for the person
     */
    List<Availability> findByPersonPersonId(Integer personId);
    
    /**
     * Finds all availability periods that overlap with a given date range.
     * 
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of availability periods that overlap with the given range
     */
    List<Availability> findByFromDateLessThanEqualAndToDateGreaterThanEqual(
        LocalDate endDate, LocalDate startDate);
}
