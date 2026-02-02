package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Role entities.
 * Provides CRUD operations and custom queries for role data.
 * 
 * @author IV1201 Team
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Finds a role by its name.
     * Used for authentication and authorization.
     * 
     * @param name the name of the role (e.g., "recruiter", "applicant")
     * @return the role with the given name, or null if not found
     */
    Role findByName(String name);
}
