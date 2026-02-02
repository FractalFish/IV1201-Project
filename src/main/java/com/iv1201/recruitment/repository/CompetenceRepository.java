package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Competence entities.
 * Provides CRUD operations and custom queries for competence data.
 * 
 * @author IV1201 Team
 */
@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Integer> {
    
    /**
     * Finds a competence by its name.
     * 
     * @param name the name of the competence to find
     * @return the competence with the given name, or null if not found
     */
    Competence findByName(String name);
}
