package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.CompetenceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CompetenceProfile entities.
 * Provides CRUD operations and custom queries for competence profile data.
 * 
 * @author IV1201 Team
 */
@Repository
public interface CompetenceProfileRepository extends JpaRepository<CompetenceProfile, Integer> {
    
    /**
     * Finds all competence profiles for a specific person.
     * 
     * @param personId the ID of the person
     * @return list of competence profiles for the person
     */
    List<CompetenceProfile> findByPersonPersonId(Integer personId);
    
    /**
     * Finds all competence profiles for a specific competence.
     * 
     * @param competenceId the ID of the competence
     * @return list of competence profiles for the competence
     */
    List<CompetenceProfile> findByCompetenceCompetenceId(Integer competenceId);

    /**
     * Deletes all competence profiles for a specific person.
     * 
     * @param personId the ID of the person
     */
    void deleteByPersonPersonId(Integer personId);
}
