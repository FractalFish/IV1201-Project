package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.ApplicationStatus;
import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ApplicationRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class ApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Person testPerson;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("applicant");
        roleRepository.save(role);

        testPerson = new Person();
        testPerson.setUsername("testuser");
        testPerson.setPassword("password");
        testPerson.setName("Kalle");
        testPerson.setSurname("Anka");
        testPerson.setRole(role);
        personRepository.save(testPerson);
    }

    /**
     * Verifies that an application can be saved and retrieved by person.
     */
    @Test
    void testFindByPerson() {
        Application application = new Application(testPerson);
        application.setStatus(ApplicationStatus.UNHANDLED);
        entityManager.persist(application);
        entityManager.flush();

        Optional<Application> found = applicationRepository.findByPerson(testPerson);

        assertTrue(found.isPresent());
        assertEquals(testPerson.getUsername(), found.get().getPerson().getUsername());
    }

    /**
     * Verifies that findByPerson returns empty when no application exists.
     */
    @Test
    void testFindByPersonNotFound() {
        Optional<Application> found = applicationRepository.findByPerson(testPerson);

        assertTrue(found.isEmpty());
    }

    /**
     * Verifies that an application can be found by person ID.
     */
    @Test
    void testFindByPersonPersonId() {
        Application application = new Application(testPerson);
        entityManager.persist(application);
        entityManager.flush();

        Optional<Application> found = applicationRepository.findByPersonPersonId(testPerson.getPersonId());

        assertTrue(found.isPresent());
    }

    /**
     * Verifies that applications can be filtered by status.
     */
    @Test
    void testFindByStatus() {
        Application app1 = new Application(testPerson);
        app1.setStatus(ApplicationStatus.UNHANDLED);
        
        Person person2 = new Person();
        person2.setUsername("user2");
        person2.setPassword("password");
        person2.setRole(testPerson.getRole());
        personRepository.save(person2);
        
        Application app2 = new Application(person2);
        app2.setStatus(ApplicationStatus.ACCEPTED);

        entityManager.persist(app1);
        entityManager.persist(app2);
        entityManager.flush();

        List<Application> unhandled = applicationRepository.findByStatus(ApplicationStatus.UNHANDLED);

        assertEquals(1, unhandled.size());
        assertEquals(ApplicationStatus.UNHANDLED, unhandled.get(0).getStatus());
    }

    /**
     * Verifies that applications are returned in descending order by created date.
     */
    @Test
    void testFindAllByOrderByCreatedAtDesc() {
        Application app = new Application(testPerson);
        entityManager.persist(app);
        entityManager.flush();

        List<Application> all = applicationRepository.findAllByOrderByCreatedAtDesc();

        assertFalse(all.isEmpty());
    }

    /**
     * Verifies that existsByPerson returns true when application exists.
     */
    @Test
    void testExistsByPerson() {
        Application application = new Application(testPerson);
        entityManager.persist(application);
        entityManager.flush();

        boolean exists = applicationRepository.existsByPerson(testPerson);

        assertTrue(exists);
    }

    /**
     * Verifies that existsByPerson returns false when no application exists.
     */
    @Test
    void testExistsByPersonFalse() {
        boolean exists = applicationRepository.existsByPerson(testPerson);

        assertFalse(exists);
    }

    /**
     * Verifies that application status can be updated.
     */
    @Test
    void testApplicationStatusUpdate() {
        Application application = new Application(testPerson);
        application.setStatus(ApplicationStatus.UNHANDLED);
        entityManager.persist(application);
        entityManager.flush();

        Application saved = applicationRepository.findByPerson(testPerson).orElseThrow();
        saved.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(saved);
        entityManager.flush();

        Application updated = applicationRepository.findByPerson(testPerson).orElseThrow();
        assertEquals(ApplicationStatus.ACCEPTED, updated.getStatus());
    }
}
