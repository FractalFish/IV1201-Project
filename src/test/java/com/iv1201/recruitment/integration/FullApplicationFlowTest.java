package com.iv1201.recruitment.integration;

import com.iv1201.recruitment.domain.*;
import com.iv1201.recruitment.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for application flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FullApplicationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CompetenceProfileRepository competenceProfileRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Person testApplicant;
    private Competence testCompetence;

    @BeforeEach
    void setUp() {
        availabilityRepository.deleteAll();
        applicationRepository.deleteAll();
        personRepository.deleteAll();
        competenceRepository.deleteAll();
        roleRepository.deleteAll();

        Role applicantRole = new Role();
        applicantRole.setName("applicant");
        roleRepository.save(applicantRole);

        Role recruiterRole = new Role();
        recruiterRole.setName("recruiter");
        roleRepository.save(recruiterRole);

        testApplicant = new Person();
        testApplicant.setUsername("applicant");
        testApplicant.setPassword(passwordEncoder.encode("password"));
        testApplicant.setName("Kalle");
        testApplicant.setSurname("Anka");
        testApplicant.setRole(applicantRole);
        personRepository.save(testApplicant);

        testCompetence = new Competence();
        testCompetence.setName("Java Programming");
        competenceRepository.save(testCompetence);
    }

    /**
     * Tests that login page is accessible.
     */
    @Test
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    /**
     * Tests that application can be created and retrieved.
     */
    @Test
    void testApplicationCreationAndRetrieval() {
        Application application = new Application(testApplicant);
        application.setStatus(ApplicationStatus.UNHANDLED);
        applicationRepository.save(application);

        Application saved = applicationRepository.findByPerson(testApplicant).orElse(null);
        assertNotNull(saved);
        assertEquals(ApplicationStatus.UNHANDLED, saved.getStatus());
    }

    /**
     * Tests that competence profile can be saved with application.
     */
    @Test
    void testCompetenceProfileSaved() {
        Application application = new Application(testApplicant);
        application.setStatus(ApplicationStatus.UNHANDLED);
        applicationRepository.save(application);

        CompetenceProfile profile = new CompetenceProfile();
        profile.setPerson(testApplicant);
        profile.setCompetence(testCompetence);
        profile.setYearsOfExperience(BigDecimal.valueOf(5.0));
        competenceProfileRepository.save(profile);

        List<CompetenceProfile> profiles = competenceProfileRepository.findByPersonPersonId(testApplicant.getPersonId());
        assertFalse(profiles.isEmpty());
        assertEquals(0, BigDecimal.valueOf(5.0).compareTo(profiles.get(0).getYearsOfExperience()));
    }

    /**
     * Tests that availability can be saved with application.
     */
    @Test
    void testAvailabilitySaved() {
        Application application = new Application(testApplicant);
        application.setStatus(ApplicationStatus.UNHANDLED);
        applicationRepository.save(application);

        Availability availability = new Availability();
        availability.setPerson(testApplicant);
        availability.setFromDate(LocalDate.of(2025, 6, 1));
        availability.setToDate(LocalDate.of(2025, 8, 31));
        availabilityRepository.save(availability);

        List<Availability> availabilities = availabilityRepository.findByPersonPersonId(testApplicant.getPersonId());
        assertFalse(availabilities.isEmpty());
    }

    /**
     * Tests that application status can be updated.
     */
    @Test
    void testApplicationStatusUpdate() {
        Application application = new Application(testApplicant);
        application.setStatus(ApplicationStatus.UNHANDLED);
        applicationRepository.save(application);

        Application saved = applicationRepository.findByPerson(testApplicant).orElseThrow();
        saved.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(saved);

        Application updated = applicationRepository.findByPerson(testApplicant).orElseThrow();
        assertEquals(ApplicationStatus.ACCEPTED, updated.getStatus());
    }

    /**
     * Tests that applications can be filtered by status.
     */
    @Test
    void testApplicationsFilteredByStatus() {
        Application app1 = new Application(testApplicant);
        app1.setStatus(ApplicationStatus.UNHANDLED);
        
        Person person2 = new Person();
        person2.setUsername("user2");
        person2.setPassword("password");
        person2.setRole(testApplicant.getRole());
        personRepository.save(person2);
        
        Application app2 = new Application(person2);
        app2.setStatus(ApplicationStatus.ACCEPTED);

        applicationRepository.save(app1);
        applicationRepository.save(app2);

        List<Application> unhandled = applicationRepository.findByStatus(ApplicationStatus.UNHANDLED);
        assertEquals(1, unhandled.size());
    }
}
