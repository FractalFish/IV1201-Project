package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PersonRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class PersonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role applicantRole;

    @BeforeEach
    void setUp() {
        applicantRole = new Role();
        applicantRole.setName("applicant");
        roleRepository.save(applicantRole);
    }

    /**
     * Verifies that a person can be saved and retrieved by username.
     */
    @Test
    void testFindByUsername() {
        Person person = new Person();
        person.setUsername("testuser");
        person.setPassword("password123");
        person.setName("Kalle");
        person.setSurname("Anka");
        person.setEmail("kalle.anka@example.com");
        person.setRole(applicantRole);
        entityManager.persist(person);
        entityManager.flush();

        Person found = personRepository.findByUsername("testuser").orElse(null);

        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
        assertEquals("Kalle", found.getName());
    }

    /**
     * Verifies that findByUsername returns empty when user does not exist.
     */
    @Test
    void testFindByUsernameNotFound() {
        Person found = personRepository.findByUsername("nonexistent").orElse(null);

        assertNull(found);
    }

    /**
     * Verifies that a person can be saved and retrieved by email.
     */
    @Test
    void testFindByEmail() {
        Person person = new Person();
        person.setUsername("testuser");
        person.setEmail("kalle.anka@example.com");
        person.setPassword("password");
        person.setRole(applicantRole);
        entityManager.persist(person);
        entityManager.flush();

        Person found = personRepository.findByEmail("kalle.anka@example.com").orElse(null);

        assertNotNull(found);
        assertEquals("kalle.anka@example.com", found.getEmail());
    }

    /**
     * Verifies that existsByUsername returns true for existing user.
     */
    @Test
    void testExistsByUsername() {
        Person person = new Person();
        person.setUsername("existinguser");
        person.setPassword("password");
        person.setRole(applicantRole);
        entityManager.persist(person);
        entityManager.flush();

        boolean exists = personRepository.existsByUsername("existinguser");

        assertTrue(exists);
    }

    /**
     * Verifies that existsByUsername returns false for non-existing user.
     */
    @Test
    void testExistsByUsernameFalse() {
        boolean exists = personRepository.existsByUsername("newuser");

        assertFalse(exists);
    }

    /**
     * Verifies that existsByEmail returns true for existing email.
     */
    @Test
    void testExistsByEmail() {
        Person person = new Person();
        person.setUsername("testuser");
        person.setEmail("test@example.com");
        person.setPassword("password");
        person.setRole(applicantRole);
        entityManager.persist(person);
        entityManager.flush();

        boolean exists = personRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    /**
     * Verifies that existsByEmail returns false for non-existing email.
     */
    @Test
    void testExistsByEmailFalse() {
        boolean exists = personRepository.existsByEmail("new@example.com");

        assertFalse(exists);
    }

    /**
     * Verifies that person can have null email.
     */
    @Test
    void testPersonWithNullEmail() {
        Person person = new Person();
        person.setUsername("noemail");
        person.setPassword("password");
        person.setEmail(null);
        person.setRole(applicantRole);
        entityManager.persist(person);
        entityManager.flush();

        Person found = personRepository.findByUsername("noemail").orElse(null);

        assertNotNull(found);
        assertNull(found.getEmail());
    }
}
