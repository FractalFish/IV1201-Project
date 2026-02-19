package com.iv1201.recruitment.exception;

/**
 * Exception thrown when a registration attempt uses a username that already exists.
 */
public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String username) {
        super("Username already taken: " + username);
    }
}
