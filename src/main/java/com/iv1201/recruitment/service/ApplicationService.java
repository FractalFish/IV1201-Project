package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.ApplicationStatus;
import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing job applications.
 * Handles application lifecycle: creation, retrieval, and status updates.
 * All methods are transactional for data consistency.
 */
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    /**
     * Constructs an ApplicationService with required dependencies.
     *
     * @param applicationRepository repository for application entities
     */
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Creates a new application for an applicant.
     *
     * @param person the applicant submitting the application
     * @return the created application
     * @throws IllegalStateException if the person already has an application
     */
    @Transactional
    public Application createApplication(Person person) {
        if (applicationRepository.existsByPerson(person)) {
            throw new IllegalStateException("Application already exists for this user");
        }
        
        Application application = new Application(person);
        return applicationRepository.save(application);
    }

    /**
     * Finds an application by its ID.
     *
     * @param applicationId the application ID
     * @return the application if found
     */
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationById(Integer applicationId) {
        return applicationRepository.findById(applicationId);
    }

    /**
     * Finds an application by the associated person.
     *
     * @param person the applicant
     * @return the application if found
     */
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationByPerson(Person person) {
        return applicationRepository.findByPerson(person);
    }

    /**
     * Finds an application by person ID.
     *
     * @param personId the person's ID
     * @return the application if found
     */
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationByPersonId(Integer personId) {
        return applicationRepository.findByPersonPersonId(personId);
    }

    /**
     * Retrieves all applications, optionally filtered by status.
     *
     * @param status the status filter, or null for all applications
     * @return list of applications
     */
    @Transactional(readOnly = true)
    public List<Application> getApplications(ApplicationStatus status) {
        if (status != null) {
            return applicationRepository.findByStatus(status);
        }
        return applicationRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Updates the status of an application.
     * Uses optimistic locking via @Version to prevent concurrent modifications.
     *
     * @param applicationId the application ID
     * @param newStatus the new status
     * @return the updated application
     * @throws IllegalArgumentException if application not found
     */
    @Transactional
    public Application updateStatus(Integer applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }

    /**
     * Checks if a person already has an application.
     *
     * @param person the person to check
     * @return true if application exists
     */
    @Transactional(readOnly = true)
    public boolean hasApplication(Person person) {
        return applicationRepository.existsByPerson(person);
    }
}
