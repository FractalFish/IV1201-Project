package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import com.iv1201.recruitment.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private AuthService authService;

    private Person testPerson;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setRoleId(1);
        testRole.setName("applicant");

        testPerson = new Person();
        testPerson.setPersonId(1);
        testPerson.setUsername("testuser");
        testPerson.setPassword("$2a$10$hashedpassword");
        testPerson.setName("Test");
        testPerson.setSurname("User");
        testPerson.setRole(testRole);
    }

    /**
     * Verifies that a user can log in with correct username and password.
     */
    @Test
    void testValidLogin() {
        when(personRepository.findByUsername("testuser")).thenReturn(Optional.of(testPerson));

        UserDetails userDetails = authService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("$2a$10$hashedpassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_APPLICANT")));
    }

    /**
     * Verifies that login fails when user does not exist.
     */
    @Test
    void testUserNotFound() {
        when(personRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("nonexistent")
        );
        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    /**
     * Verifies that recruiter users get the correct role authority.
     */
    @Test
    void testRecruiterRole() {
        Role recruiterRole = new Role();
        recruiterRole.setRoleId(2);
        recruiterRole.setName("recruiter");
        
        Person recruiterPerson = new Person();
        recruiterPerson.setPersonId(2);
        recruiterPerson.setUsername("recruiteruser");
        recruiterPerson.setPassword("$2a$10$hash");
        recruiterPerson.setRole(recruiterRole);

        when(personRepository.findByUsername("recruiteruser")).thenReturn(Optional.of(recruiterPerson));

        UserDetails userDetails = authService.loadUserByUsername("recruiteruser");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_RECRUITER")));
    }

    /**
     * Verifies that role name is converted to uppercase with ROLE_ prefix.
     */
    @Test
    void testRoleAuthorityFormat() {
        when(personRepository.findByUsername("testuser")).thenReturn(Optional.of(testPerson));

        UserDetails userDetails = authService.loadUserByUsername("testuser");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_APPLICANT")));
    }
}
