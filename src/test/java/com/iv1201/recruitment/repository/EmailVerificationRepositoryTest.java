package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.EmailVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EmailVerificationRepositoryTest {

    @Autowired
    private EmailVerificationRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testSaveAndFindByToken() {
        EmailVerification verification = new EmailVerification(
            "test@example.com",
            "test-token-123",
            LocalDateTime.now().plusHours(24)
        );
        repository.save(verification);

        Optional<EmailVerification> found = repository.findByToken("test-token-123");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        EmailVerification verification = new EmailVerification(
            "test@example.com",
            "test-token-123",
            LocalDateTime.now().plusHours(24)
        );
        repository.save(verification);

        Optional<EmailVerification> found = repository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
    }

    @Test
    void testDeleteByEmail() {
        EmailVerification verification = new EmailVerification(
            "test@example.com",
            "test-token-123",
            LocalDateTime.now().plusHours(24)
        );
        repository.save(verification);

        repository.deleteByEmail("test@example.com");

        Optional<EmailVerification> found = repository.findByEmail("test@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByTokenNotFound() {
        Optional<EmailVerification> found = repository.findByToken("nonexistent");

        assertTrue(found.isEmpty());
    }
}
