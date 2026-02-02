-- ===================================================================
-- Password Migration: Hash plaintext passwords with BCrypt
-- ===================================================================
-- This script converts all plaintext passwords to BCrypt hashes
-- Requires pgcrypto extension (available in PostgreSQL by default)
-- 
-- IMPORTANT: This is a one-way migration. Original passwords cannot
-- be recovered after this script runs.
-- ===================================================================

-- Enable pgcrypto extension for BCrypt support
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Hash all plaintext passwords using BCrypt (cost factor 10)
-- Only updates passwords that:
--   1. Are not NULL
--   2. Are not empty
--   3. Don't already look like BCrypt hashes ($2a$ or $2b$ prefix)
UPDATE person 
SET password = crypt(password, gen_salt('bf', 10))
WHERE password IS NOT NULL 
  AND password != ''
  AND password NOT LIKE '$2a$%'
  AND password NOT LIKE '$2b$%';

-- Log the result
DO $$
DECLARE
    hashed_count INTEGER;
    null_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO hashed_count 
    FROM person 
    WHERE password LIKE '$2a$%' OR password LIKE '$2b$%';
    
    SELECT COUNT(*) INTO null_count 
    FROM person 
    WHERE password IS NULL;
    
    RAISE NOTICE 'Password migration complete: % hashed, % without passwords', 
                 hashed_count, null_count;
END $$;
