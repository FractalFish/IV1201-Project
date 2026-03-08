package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.EmailVerification;
import com.iv1201.recruitment.repository.EmailVerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository repository;

    public EmailVerificationService(EmailVerificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String generateToken(String email) {
        repository.deleteByEmail(email);
        
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        
        EmailVerification verification = new EmailVerification(email, token, expiresAt);
        repository.save(verification);
        
        return token;
    }

    @Transactional(readOnly = true)
    public Optional<EmailVerification> validateToken(String token) {
        Optional<EmailVerification> verification = repository.findByToken(token);
        
        if (verification.isPresent()) {
            EmailVerification v = verification.get();
            if (v.getUsed() || v.isExpired()) {
                return Optional.empty();
            }
            return verification;
        }
        
        return Optional.empty();
    }

    @Transactional
    public void markAsUsed(String token) {
        repository.findByToken(token).ifPresent(v -> {
            v.setUsed(true);
            repository.save(v);
        });
    }

    @Transactional(readOnly = true)
    public Optional<String> getEmailByToken(String token) {
        return repository.findByToken(token)
                .filter(v -> !v.getUsed() && !v.isExpired())
                .map(EmailVerification::getEmail);
    }
}
