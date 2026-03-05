package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {

    Optional<EmailVerification> findByToken(String token);

    Optional<EmailVerification> findByEmail(String email);

    void deleteByEmail(String email);
}
