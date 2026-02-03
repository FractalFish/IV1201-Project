-- Migration: Add application table for tracking application status
-- This table links to the existing person table and adds status tracking
-- with optimistic locking support via version column.

CREATE TABLE IF NOT EXISTS application (
    application_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    person_id INTEGER UNIQUE REFERENCES person(person_id),
    status VARCHAR(20) NOT NULL DEFAULT 'UNHANDLED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

-- Create index for faster status queries
CREATE INDEX IF NOT EXISTS idx_application_status ON application(status);

-- Migrate existing applicants: Create application records for all applicants
-- who have competence profiles (indicating they've applied)
INSERT INTO application (person_id, status, created_at, updated_at, version)
SELECT DISTINCT cp.person_id, 'UNHANDLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM competence_profile cp
JOIN person p ON cp.person_id = p.person_id
JOIN role r ON p.role_id = r.role_id
WHERE r.name = 'applicant'
ON CONFLICT (person_id) DO NOTHING;
