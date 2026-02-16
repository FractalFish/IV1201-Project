package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.*;
import com.iv1201.recruitment.domain.dto.*;
import com.iv1201.recruitment.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing job applications.
 * Handles application lifecycle: creation, retrieval, and status updates.
 * All methods are transactional for data consistency.
 */
@Service
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final CompetenceRepository competenceRepository;
    private final CompetenceProfileRepository competenceProfileRepository;
    private final AvailabilityRepository availabilityRepository;

    /**
     * Constructs an ApplicationService with required dependencies.
     *
     * @param applicationRepository repository for application entities
     * @param competenceRepository repository for competence entities
     * @param competenceProfileRepository repository for competence profile entities
     * @param availabilityRepository repository for availability entities
     */
    public ApplicationService(ApplicationRepository applicationRepository,
                              CompetenceRepository competenceRepository,
                              CompetenceProfileRepository competenceProfileRepository,
                              AvailabilityRepository availabilityRepository) {
        this.applicationRepository = applicationRepository;
        this.competenceRepository = competenceRepository;
        this.competenceProfileRepository = competenceProfileRepository;
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * Retrieves all available competences.
     *
     * @return list of all competences
     */
    @Transactional(readOnly = true)
    public List<Competence> getAllCompetences() {
        return competenceRepository.findAll();
    }

    /**
     * Submits a new application with competences and availabilities.
     * If the person already has an application, their competences and availabilities
     * are replaced with the new ones.
     *
     * @param person the applicant
     * @param form the application form with competences and availabilities
     * @return the created or updated application
     */
    @Transactional
    public Application submitApplication(Person person, ApplicationFormDTO form) {
        logger.info("Submitting application for person: personId={}, username={}", 
            person.getPersonId(), person.getUsername());
        
        // Delete existing competence profiles and availabilities
        competenceProfileRepository.deleteByPersonPersonId(person.getPersonId());
        availabilityRepository.deleteByPersonPersonId(person.getPersonId());
        logger.debug("Cleared existing competence profiles and availabilities for personId={}", person.getPersonId());

        // Create new competence profiles
        int competenceCount = 0;
        if (form.getCompetences() != null) {
            for (CompetenceForm cf : form.getCompetences()) {
                if (cf.getCompetenceId() != null && cf.getYearsOfExperience() != null) {
                    Competence competence = competenceRepository.findById(cf.getCompetenceId())
                            .orElseThrow(() -> {
                                logger.warn("Invalid competence ID attempted: {}", cf.getCompetenceId());
                                return new IllegalArgumentException("Invalid competence ID: " + cf.getCompetenceId());
                            });
                    
                    CompetenceProfile profile = new CompetenceProfile();
                    profile.setPerson(person);
                    profile.setCompetence(competence);
                    profile.setYearsOfExperience(cf.getYearsOfExperience());
                    competenceProfileRepository.save(profile);
                    competenceCount++;
                }
            }
        }
        logger.info("Created {} competence profiles for personId={}", competenceCount, person.getPersonId());

        // Create new availabilities
        int availabilityCount = 0;
        if (form.getAvailabilities() != null) {
            for (AvailabilityForm af : form.getAvailabilities()) {
                if (af.getFromDate() != null && af.getToDate() != null) {
                    if (!af.isValid()) {
                        logger.warn("Invalid date range in application: fromDate={}, toDate={}", 
                            af.getFromDate(), af.getToDate());
                        throw new IllegalArgumentException("Invalid date range: toDate must be after fromDate");
                    }
                    
                    Availability availability = new Availability();
                    availability.setPerson(person);
                    availability.setFromDate(af.getFromDate());
                    availability.setToDate(af.getToDate());
                    availabilityRepository.save(availability);
                    availabilityCount++;
                }
            }
        }
        logger.info("Created {} availabilities for personId={}", availabilityCount, person.getPersonId());

        // Create or update application
        Application application = applicationRepository.findByPerson(person)
                .orElse(new Application(person));
        
        Application savedApplication = applicationRepository.save(application);
        logger.info("Application saved successfully: applicationId={}, personId={}, username={}", 
            savedApplication.getApplicationId(), person.getPersonId(), person.getUsername());
        
        return savedApplication;
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
     * Gets full application details including competences and availabilities.
     *
     * @param applicationId the application ID
     * @return the application details DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<ApplicationDetailsDTO> getApplicationDetails(Integer applicationId) {
        logger.info("Getting application details for id={}", applicationId);
        Optional<Application> appOpt = applicationRepository.findById(applicationId);
        
        if (appOpt.isEmpty()) {
            logger.warn("Application not found for id={}", applicationId);
            return Optional.empty();
        }
        
        Application app = appOpt.get();
        // Force eager loading of all fields within the transaction
        Integer appId = app.getApplicationId();
        Person person = app.getPerson();
        String personName = person.getName() + " " + person.getSurname();
        String email = person.getEmail();
        String pnr = person.getPnr();
        Integer personId = person.getPersonId();
        ApplicationStatus status = app.getStatus();
        LocalDateTime createdAt = app.getCreatedAt();
        LocalDateTime updatedAt = app.getUpdatedAt();
        Integer version = app.getVersion();
        
        logger.info("Application loaded: id={}, personName={}, status={}", appId, personName, status);
        
        // Build DTO within transaction
        ApplicationDetailsDTO dto = new ApplicationDetailsDTO();
        dto.setApplicationId(appId);
        dto.setPersonName(personName);
        dto.setPersonEmail(email);
        dto.setPersonPnr(pnr);
        dto.setStatus(status);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        dto.setVersion(version);

        // Get competence profiles
        List<CompetenceProfile> profiles = competenceProfileRepository.findByPersonPersonId(personId);
        logger.info("Found {} competence profiles", profiles.size());
        dto.setCompetences(profiles.stream()
                .map(p -> new CompetenceDetailDTO(p.getCompetence().getName(), p.getYearsOfExperience()))
                .collect(Collectors.toList()));

        // Get availabilities
        List<Availability> availabilities = availabilityRepository.findByPersonPersonId(personId);
        logger.info("Found {} availabilities", availabilities.size());
        dto.setAvailabilities(availabilities.stream()
                .map(a -> new AvailabilityDetailDTO(a.getFromDate(), a.getToDate()))
                .collect(Collectors.toList()));

        return Optional.of(dto);
    }

    /**
     * Retrieves all applications as list DTOs.
     *
     * @return list of application list DTOs
     */
    @Transactional(readOnly = true)
    public List<ApplicationListDTO> getAllApplications() {
        return applicationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications as page of DTOs with pagination.
     *
     * @param pageable pagination information
     * @return page of application list DTOs
     */
    @Transactional(readOnly = true)
    public Page<ApplicationListDTO> getAllApplications(Pageable pageable) {
        return applicationRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toListDTO);
    }

    /**
     * Retrieves applications filtered by status.
     *
     * @param status the status filter
     * @return list of application list DTOs with that status
     */
    @Transactional(readOnly = true)
    public List<ApplicationListDTO> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves applications filtered by status with pagination.
     *
     * @param status the status filter
     * @param pageable pagination information
     * @return page of application list DTOs with that status
     */
    @Transactional(readOnly = true)
    public Page<ApplicationListDTO> getApplicationsByStatus(ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByStatus(status, pageable)
                .map(this::toListDTO);
    }

    /**
     * Retrieves all applications, optionally filtered by status.
     * Returns Application entities for backward compatibility.
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
     * Updates the status of an application with optimistic locking.
     *
     * @param applicationId the application ID
     * @param newStatus the new status
     * @param expectedVersion the expected version for optimistic locking
     * @return the updated application
     * @throws IllegalArgumentException if application not found
     * @throws ObjectOptimisticLockingFailureException if version mismatch
     */
    @Transactional
    public Application updateApplicationStatus(Integer applicationId, ApplicationStatus newStatus, Integer expectedVersion) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        
        if (expectedVersion != null && !expectedVersion.equals(application.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Application.class, applicationId);
        }
        
        application.setStatus(newStatus);
        return applicationRepository.save(application);
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
        return updateApplicationStatus(applicationId, newStatus, null);
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

    /**
     * Converts an Application to ApplicationDetailsDTO.
     */
    private ApplicationDetailsDTO toDetailsDTO(Application app) {
        ApplicationDetailsDTO dto = new ApplicationDetailsDTO();
        dto.setApplicationId(app.getApplicationId());
        dto.setPersonName(app.getPerson().getName() + " " + app.getPerson().getSurname());
        dto.setPersonEmail(app.getPerson().getEmail());
        dto.setPersonPnr(app.getPerson().getPnr());
        dto.setStatus(app.getStatus());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());
        dto.setVersion(app.getVersion());

        // Get competence profiles
        List<CompetenceProfile> profiles = competenceProfileRepository
                .findByPersonPersonId(app.getPerson().getPersonId());
        dto.setCompetences(profiles.stream()
                .map(p -> new CompetenceDetailDTO(p.getCompetence().getName(), p.getYearsOfExperience()))
                .collect(Collectors.toList()));

        // Get availabilities
        List<Availability> availabilities = availabilityRepository
                .findByPersonPersonId(app.getPerson().getPersonId());
        dto.setAvailabilities(availabilities.stream()
                .map(a -> new AvailabilityDetailDTO(a.getFromDate(), a.getToDate()))
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * Converts an Application to ApplicationListDTO.
     */
    private ApplicationListDTO toListDTO(Application app) {
        return new ApplicationListDTO(
                app.getApplicationId(),
                app.getPerson().getName() + " " + app.getPerson().getSurname(),
                app.getStatus(),
                app.getCreatedAt()
        );
    }
}
