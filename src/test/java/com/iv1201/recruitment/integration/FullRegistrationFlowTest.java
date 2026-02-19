package com.iv1201.recruitment.integration;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for registration flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FullRegistrationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        roleRepository.deleteAll();

        Role applicantRole = new Role();
        applicantRole.setName("applicant");
        roleRepository.save(applicantRole);
    }

    /**
     * Tests that registration page is accessible.
     */
    @Test
    void testRegistrationPageAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    /**
     * Tests that person can be saved and retrieved from database.
     */
    @Test
    void testPersonSaveAndRetrieve() {
        Role role = roleRepository.findByName("applicant");

        Person person = new Person();
        person.setUsername("testuser");
        person.setPassword(passwordEncoder.encode("password"));
        person.setName("Kalle");
        person.setSurname("Anka");
        person.setEmail("kalle.anka@example.com");
        person.setRole(role);
        
        Person saved = personRepository.save(person);
        assertNotNull(saved.getPersonId());

        Person found = personRepository.findByUsername("testuser").orElse(null);
        assertNotNull(found);
        assertEquals("Kalle", found.getName());
    }

    /**
     * Tests that duplicate username is detected.
     */
    @Test
    void testDuplicateUsernameDetection() throws Exception {
        Role role = roleRepository.findByName("applicant");

        Person person = new Person();
        person.setUsername("existinguser");
        person.setPassword("password");
        person.setRole(role);
        personRepository.save(person);

        boolean exists = personRepository.existsByUsername("existinguser");
        assertTrue(exists);
    }

    /**
     * Tests that login page works.
     */
    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
