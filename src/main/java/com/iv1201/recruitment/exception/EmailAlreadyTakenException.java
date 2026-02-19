package com.iv1201.recruitment.exception;

/**
 * Exception thrown when a registration attempt uses an email that is already registered.
 */
public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(String email) {
        super("Email already taken: " + email);
    }
}
