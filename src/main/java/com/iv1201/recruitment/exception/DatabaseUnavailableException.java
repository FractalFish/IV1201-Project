package com.iv1201.recruitment.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when the database is unavailable during authentication.
 * This allows distinguishing database connectivity issues from invalid credentials.
 */
public class DatabaseUnavailableException extends AuthenticationException {
    
    public DatabaseUnavailableException(String msg) {
        super(msg);
    }
    
    public DatabaseUnavailableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
