package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.EmailVerification;
import com.iv1201.recruitment.repository.EmailVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationRepository repository;

    @InjectMocks
    private EmailVerificationService service;

    private EmailVerification validVerification;

    @BeforeEach
    void setUp() {
        validVerification = new EmailVerification(
            "test@example.com", 
            "test-token-123", 
            LocalDateTime.now().plusHours(24)
        );
    }

    @Test
    void testGenerateToken() {
        when(repository.deleteByEmail(anyString())).thenReturn(0);
        when(repository.save(any(EmailVerification.class))).thenAnswer(i -> i.getArgument(0));

        String token = service.generateToken("test@example.com");

        assertNotNull(token);
        verify(repository).save(any(EmailVerification.class));
    }

    @Test
    void testValidateToken_Valid() {
        when(repository.findByToken("valid-token")).thenReturn(Optional.of(validVerification));

        Optional<EmailVerification> result = service.validateToken("valid-token");

        assertTrue(result.isPresent());
    }

    @Test
    void testValidateToken_Expired() {
        EmailVerification expired = new EmailVerification(
            "test@example.com", 
            "expired-token", 
            LocalDateTime.now().minusHours(1)
        );
        when(repository.findByToken("expired-token")).thenReturn(Optional.of(expired));

        Optional<EmailVerification> result = service.validateToken("expired-token");

        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateToken_Used() {
        validVerification.setUsed(true);
        when(repository.findByToken("used-token")).thenReturn(Optional.of(validVerification));

        Optional<EmailVerification> result = service.validateToken("used-token");

        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateToken_NotFound() {
        when(repository.findByToken("nonexistent")).thenReturn(Optional.empty());

        Optional<EmailVerification> result = service.validateToken("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testMarkAsUsed() {
        when(repository.findByToken("token-to-mark")).thenReturn(Optional.of(validVerification));
        when(repository.save(any(EmailVerification.class))).thenAnswer(i -> i.getArgument(0));

        service.markAsUsed("token-to-mark");

        assertTrue(validVerification.getUsed());
    }
}
