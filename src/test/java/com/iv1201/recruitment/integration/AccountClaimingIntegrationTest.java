package com.iv1201.recruitment.integration;

import com.iv1201.recruitment.domain.EmailVerification;
import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import com.iv1201.recruitment.repository.EmailVerificationRepository;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AccountClaimingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailVerificationRepository verificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        verificationRepository.deleteAll();
        personRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = new Role();
        role.setName("applicant");
        roleRepository.save(role);
    }

    @Test
    void testClaimPageAccessible() throws Exception {
        mockMvc.perform(get("/claim"))
                .andExpect(status().isOk())
                .andExpect(view().name("claim"));
    }

    @Test
    void testVerifyPageWithInvalidToken() throws Exception {
        mockMvc.perform(get("/verify").param("token", "invalid-token"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testClaimWithNoApplication() throws Exception {
        mockMvc.perform(post("/claim")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testClaimWithLegacyUser() throws Exception {
        Person person = new Person();
        person.setEmail("legacy@example.com");
        person.setName("Legacy");
        person.setSurname("User");
        person.setPassword("");
        person.setRole(roleRepository.findByName("applicant"));
        personRepository.save(person);

        mockMvc.perform(post("/claim")
                        .param("email", "legacy@example.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"));
    }
}
