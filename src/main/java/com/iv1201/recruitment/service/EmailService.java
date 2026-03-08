package com.iv1201.recruitment.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
}
