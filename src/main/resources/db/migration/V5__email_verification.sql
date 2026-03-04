-- Email verification table for legacy user account claiming

CREATE TABLE email_verification (
    token_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_email_verification_token ON email_verification(token);
CREATE INDEX idx_email_verification_email ON email_verification(email);
