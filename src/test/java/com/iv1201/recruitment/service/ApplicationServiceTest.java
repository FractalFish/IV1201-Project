package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.*;
import com.iv1201.recruitment.domain.dto.*;
import com.iv1201.recruitment.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for ApplicationService.
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CompetenceRepository competenceRepository;

    @Mock
    private CompetenceProfileRepository competenceProfileRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Person testPerson;
    private Competence testCompetence;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setPersonId(1);
        testPerson.setUsername("testuser");
        testPerson.setName("Kalle");
        testPerson.setSurname("Anka");
        testPerson.setEmail("kalle.anka@example.com");
        testPerson.setPnr("199001011234");

        testCompetence = new Competence();
        testCompetence.setCompetenceId(1);
        testCompetence.setName("Java Programming");

        testApplication = new Application(testPerson);
        testApplication.setApplicationId(1);
        testApplication.setStatus(ApplicationStatus.UNHANDLED);
    }

    /**
     * Verifies that all competences can be retrieved.
     */
    @Test
    void testGetAllCompetences() {
        when(competenceRepository.findAll()).thenReturn(List.of(testCompetence));

        List<Competence> result = applicationService.getAllCompetences();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getName());
    }

    /**
     * Verifies that an application can be submitted with competences.
     */
    @Test
    void testSubmitApplicationWithCompetences() {
        CompetenceForm competenceForm = new CompetenceForm();
        competenceForm.setCompetenceId(1);
        competenceForm.setYearsOfExperience(BigDecimal.valueOf(5.0));

        ApplicationFormDTO form = new ApplicationFormDTO();
        form.setCompetences(List.of(competenceForm));

        when(competenceRepository.findById(1)).thenReturn(Optional.of(testCompetence));
        when(applicationRepository.findByPerson(testPerson)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> {
            Application app = inv.getArgument(0);
            app.setApplicationId(1);
            return app;
        });

        Application result = applicationService.submitApplication(testPerson, form);

        assertNotNull(result);
        assertEquals(1, result.getApplicationId());
    }

    /**
     * Verifies that submission fails with invalid competence.
     */
    @Test
    void testSubmitApplicationInvalidCompetence() {
        CompetenceForm competenceForm = new CompetenceForm();
        competenceForm.setCompetenceId(999);
        competenceForm.setYearsOfExperience(BigDecimal.valueOf(5.0));

        ApplicationFormDTO form = new ApplicationFormDTO();
        form.setCompetences(List.of(competenceForm));

        when(competenceRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.submitApplication(testPerson, form)
        );
        assertTrue(exception.getMessage().contains("Invalid competence ID"));
    }

    /**
     * Verifies that an application can be submitted with availability dates.
     */
    @Test
    void testSubmitApplicationWithAvailability() {
        AvailabilityForm availabilityForm = new AvailabilityForm();
        availabilityForm.setFromDate(LocalDate.of(2025, 6, 1));
        availabilityForm.setToDate(LocalDate.of(2025, 8, 31));

        ApplicationFormDTO form = new ApplicationFormDTO();
        form.setAvailabilities(List.of(availabilityForm));

        when(applicationRepository.findByPerson(testPerson)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> {
            Application app = inv.getArgument(0);
            app.setApplicationId(1);
            return app;
        });

        Application result = applicationService.submitApplication(testPerson, form);

        assertNotNull(result);
    }

    /**
     * Verifies that submission fails with invalid date range.
     */
    @Test
    void testSubmitApplicationInvalidDateRange() {
        AvailabilityForm availabilityForm = new AvailabilityForm();
        availabilityForm.setFromDate(LocalDate.of(2025, 8, 1));
        availabilityForm.setToDate(LocalDate.of(2025, 6, 1));

        ApplicationFormDTO form = new ApplicationFormDTO();
        form.setAvailabilities(List.of(availabilityForm));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.submitApplication(testPerson, form)
        );
        assertTrue(exception.getMessage().contains("Invalid date range"));
    }

    /**
     * Verifies that application can be found by ID.
     */
    @Test
    void testGetApplicationById() {
        when(applicationRepository.findById(1)).thenReturn(Optional.of(testApplication));

        Optional<Application> result = applicationService.getApplicationById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getApplicationId());
    }

    /**
     * Verifies that empty is returned when application ID does not exist.
     */
    @Test
    void testGetApplicationByIdNotFound() {
        when(applicationRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Application> result = applicationService.getApplicationById(999);

        assertTrue(result.isEmpty());
    }

    /**
     * Verifies that application can be found by person.
     */
    @Test
    void testGetApplicationByPerson() {
        when(applicationRepository.findByPerson(testPerson)).thenReturn(Optional.of(testApplication));

        Optional<Application> result = applicationService.getApplicationByPerson(testPerson);

        assertTrue(result.isPresent());
        assertEquals(testPerson.getUsername(), result.get().getPerson().getUsername());
    }

    /**
     * Verifies that application details include competences and availabilities.
     */
    @Test
    void testGetApplicationDetails() {
        Application app = new Application(testPerson);
        app.setApplicationId(1);
        app.setStatus(ApplicationStatus.UNHANDLED);
        
        CompetenceProfile profile = new CompetenceProfile();
        profile.setPerson(testPerson);
        profile.setCompetence(testCompetence);
        profile.setYearsOfExperience(BigDecimal.valueOf(3.0));

        Availability availability = new Availability();
        availability.setPerson(testPerson);
        availability.setFromDate(LocalDate.of(2025, 6, 1));
        availability.setToDate(LocalDate.of(2025, 8, 31));

        when(applicationRepository.findById(1)).thenReturn(Optional.of(app));
        when(competenceProfileRepository.findByPersonPersonId(1)).thenReturn(List.of(profile));
        when(availabilityRepository.findByPersonPersonId(1)).thenReturn(List.of(availability));

        Optional<ApplicationDetailsDTO> result = applicationService.getApplicationDetails(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getApplicationId());
        assertEquals("Java Programming", result.get().getCompetences().get(0).getCompetenceName());
        assertEquals(1, result.get().getAvailabilities().size());
    }

    /**
     * Verifies that empty is returned when application details not found.
     */
    @Test
    void testGetApplicationDetailsNotFound() {
        when(applicationRepository.findById(999)).thenReturn(Optional.empty());

        Optional<ApplicationDetailsDTO> result = applicationService.getApplicationDetails(999);

        assertTrue(result.isEmpty());
    }

    /**
     * Verifies that all applications can be retrieved.
     */
    @Test
    void testGetAllApplications() {
        Application app = new Application(testPerson);
        app.setApplicationId(1);
        
        when(applicationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(app));

        List<ApplicationListDTO> result = applicationService.getAllApplications();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Verifies that applications can be filtered by status.
     */
    @Test
    void testGetApplicationsByStatus() {
        when(applicationRepository.findByStatus(ApplicationStatus.UNHANDLED))
                .thenReturn(List.of(testApplication));

        List<ApplicationListDTO> result = applicationService.getApplicationsByStatus(ApplicationStatus.UNHANDLED);

        assertEquals(1, result.size());
        assertEquals(ApplicationStatus.UNHANDLED, result.get(0).getStatus());
    }

    /**
     * Verifies that application status can be updated.
     */
    @Test
    void testUpdateStatus() {
        when(applicationRepository.findById(1)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        Application result = applicationService.updateStatus(1, ApplicationStatus.ACCEPTED);

        assertEquals(ApplicationStatus.ACCEPTED, result.getStatus());
    }

    /**
     * Verifies that exception is thrown when updating status of non-existent application.
     */
    @Test
    void testUpdateStatusNotFound() {
        when(applicationRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.updateStatus(999, ApplicationStatus.ACCEPTED)
        );
        assertEquals("Application not found", exception.getMessage());
    }

    /**
     * Verifies that hasApplication returns true when person has an application.
     */
    @Test
    void testHasApplicationTrue() {
        when(applicationRepository.existsByPerson(testPerson)).thenReturn(true);

        boolean result = applicationService.hasApplication(testPerson);

        assertTrue(result);
    }

    /**
     * Verifies that hasApplication returns false when person has no application.
     */
    @Test
    void testHasApplicationFalse() {
        when(applicationRepository.existsByPerson(testPerson)).thenReturn(false);

        boolean result = applicationService.hasApplication(testPerson);

        assertFalse(result);
    }
}
