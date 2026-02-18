package com.iv1201.recruitment.security;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for security.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

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

        Role recruiterRole = new Role();
        recruiterRole.setName("recruiter");
        roleRepository.save(recruiterRole);

        Person applicant = new Person();
        applicant.setUsername("applicant");
        applicant.setPassword(passwordEncoder.encode("password"));
        applicant.setName("Kalle");
        applicant.setSurname("Anka");
        applicant.setRole(applicantRole);
        personRepository.save(applicant);
    }

    /**
     * Verifies that login page is accessible without authentication.
     */
    @Test
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    /**
     * Verifies that root URL redirects to login when not authenticated.
     */
    @Test
    void testRootRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Verifies that protected pages redirect to login.
     */
    @Test
    void testProtectedPagesRedirectToLogin() throws Exception {
        mockMvc.perform(get("/applicant/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Verifies that valid login succeeds and redirects to dashboard.
     */
    @Test
    void testValidLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "applicant")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * Verifies that invalid password fails login.
     */
    @Test
    void testInvalidPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "applicant")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    /**
     * Verifies that invalid username fails login.
     */
    @Test
    void testInvalidUsername() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "nonexistent")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    /**
     * Verifies that logout works.
     */
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    /**
     * Verifies that registration page is accessible.
     */
    @Test
    void testRegistrationPageAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}
