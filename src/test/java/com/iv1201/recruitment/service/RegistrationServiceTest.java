package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import com.iv1201.recruitment.domain.dto.RegistrationForm;
import com.iv1201.recruitment.exception.EmailAlreadyTakenException;
import com.iv1201.recruitment.exception.UsernameAlreadyTakenException;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for RegistrationService.
 */
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationForm validForm;
    private Role applicantRole;

    @BeforeEach
    void setUp() {
        validForm = new RegistrationForm();
        validForm.setUsername("newuser");
        validForm.setPassword("password123");
        validForm.setName("Kalle");
        validForm.setSurname("Anka");
        validForm.setEmail("kalle.anka@example.com");
        validForm.setPnr("199001011234");

        applicantRole = new Role();
        applicantRole.setRoleId(1);
        applicantRole.setName("applicant");
    }

    /**
     * Verifies that a new user can register with valid information.
     */
    @Test
    void testSuccessfulRegistration() {
        when(personRepository.existsByUsername("newuser")).thenReturn(false);
        when(personRepository.existsByEmail("kalle.anka@example.com")).thenReturn(false);
        when(roleRepository.findByName("applicant")).thenReturn(applicantRole);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedpassword");
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            Person savedPerson = invocation.getArgument(0);
            savedPerson.setPersonId(1);
            return savedPerson;
        });

        Person result = registrationService.registerApplicant(validForm);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("$2a$10$encodedpassword", result.getPassword());
        assertEquals("Kalle", result.getName());
        assertEquals("Anka", result.getSurname());
        assertEquals("kalle.anka@example.com", result.getEmail());
        assertEquals(applicantRole, result.getRole());
    }

    /**
     * Verifies that registration fails when username is already taken.
     */
    @Test
    void testDuplicateUsername() {
        when(personRepository.existsByUsername("newuser")).thenReturn(true);

        UsernameAlreadyTakenException exception = assertThrows(
                UsernameAlreadyTakenException.class,
                () -> registrationService.registerApplicant(validForm)
        );
        assertTrue(exception.getMessage().contains("newuser"));
    }

    /**
     * Verifies that registration fails when email is already registered.
     */
    @Test
    void testDuplicateEmail() {
        when(personRepository.existsByUsername("newuser")).thenReturn(false);
        when(personRepository.existsByEmail("kalle.anka@example.com")).thenReturn(true);

        EmailAlreadyTakenException exception = assertThrows(
                EmailAlreadyTakenException.class,
                () -> registrationService.registerApplicant(validForm)
        );
        assertTrue(exception.getMessage().contains("kalle.anka@example.com"));
    }

    /**
     * Verifies that registration fails when both username and email are taken.
     * Username is checked first, so UsernameAlreadyTakenException is thrown.
     */
    @Test
    void testDuplicateUsernameAndEmail() {
        when(personRepository.existsByUsername("newuser")).thenReturn(true);

        UsernameAlreadyTakenException exception = assertThrows(
                UsernameAlreadyTakenException.class,
                () -> registrationService.registerApplicant(validForm)
        );
        assertTrue(exception.getMessage().contains("newuser"));
    }

    /**
     * Verifies that registration fails when applicant role is not found in database.
     */
    @Test
    void testMissingRole() {
        when(personRepository.existsByUsername("newuser")).thenReturn(false);
        when(personRepository.existsByEmail("kalle.anka@example.com")).thenReturn(false);
        when(roleRepository.findByName("applicant")).thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> registrationService.registerApplicant(validForm)
        );
        assertTrue(exception.getMessage().contains("Applicant role not found"));
    }

    /**
     * Verifies that registration succeeds when email is null.
     */
    @Test
    void testNullEmail() {
        validForm.setEmail(null);
        when(personRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findByName("applicant")).thenReturn(applicantRole);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(personRepository.save(any(Person.class))).thenAnswer(inv -> {
            Person p = inv.getArgument(0);
            p.setPersonId(1);
            return p;
        });

        Person result = registrationService.registerApplicant(validForm);

        assertNotNull(result);
        assertNull(result.getEmail());
    }

    /**
     * Verifies that username check returns true when username exists.
     */
    @Test
    void testUsernameExists() {
        when(personRepository.existsByUsername("existinguser")).thenReturn(true);

        boolean result = registrationService.isUsernameTaken("existinguser");

        assertTrue(result);
    }

    /**
     * Verifies that username check returns false when username does not exist.
     */
    @Test
    void testUsernameDoesNotExist() {
        when(personRepository.existsByUsername("newuser")).thenReturn(false);

        boolean result = registrationService.isUsernameTaken("newuser");

        assertFalse(result);
    }

    /**
     * Verifies that email check returns true when email exists.
     */
    @Test
    void testEmailExists() {
        when(personRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = registrationService.isEmailTaken("test@example.com");

        assertTrue(result);
    }

    /**
     * Verifies that email check returns false when email does not exist.
     */
    @Test
    void testEmailDoesNotExist() {
        when(personRepository.existsByEmail("new@example.com")).thenReturn(false);

        boolean result = registrationService.isEmailTaken("new@example.com");

        assertFalse(result);
    }

    /**
     * Verifies that email check returns false for null email.
     */
    @Test
    void testNullEmailCheck() {
        boolean result = registrationService.isEmailTaken(null);

        assertFalse(result);
    }

    /**
     * Verifies that email check returns false for blank email.
     */
    @Test
    void testBlankEmailCheck() {
        boolean result = registrationService.isEmailTaken("   ");

        assertFalse(result);
    }

    /**
     * Verifies that BCrypt password encoding works correctly.
     */
    @Test
    void testPasswordEncodingWithRealBCrypt() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        
        String hash = encoder.encode("mypassword");
        
        assertTrue(encoder.matches("mypassword", hash));
        assertFalse(encoder.matches("wrongpassword", hash));
        assertTrue(hash.startsWith("$2a$"));
    }
}
