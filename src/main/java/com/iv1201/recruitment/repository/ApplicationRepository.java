package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.ApplicationStatus;
import com.iv1201.recruitment.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Application entities.
 * Provides data access operations for job applications.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    /**
     * Finds an application by the associated person.
     *
     * @param person the applicant
     * @return the application if found
     */
    Optional<Application> findByPerson(Person person);

    /**
     * Finds an application by person ID.
     *
     * @param personId the person's ID
     * @return the application if found
     */
    Optional<Application> findByPersonPersonId(Integer personId);

    /**
     * Finds all applications with a specific status.
     *
     * @param status the application status
     * @return list of applications with that status
     */
    List<Application> findByStatus(ApplicationStatus status);

    /**
     * Finds all applications ordered by creation date (newest first).
     *
     * @return list of all applications
     */
    List<Application> findAllByOrderByCreatedAtDesc();

    /**
     * Checks if an application exists for a person.
     *
     * @param person the applicant
     * @return true if application exists
     */
    boolean existsByPerson(Person person);
}
