package com.iv1201.recruitment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendVerificationEmail(String email, String token) {
        String link = "/verify?token=" + token;
        logger.info("[EMAIL SIMULATION] To: {} | Link: {}", email, link);
    }
}
