-- Test data for development
-- Passwords are BCrypt hashed (all passwords are 'password123')
-- BCrypt hash generated with: new BCryptPasswordEncoder().encode("password123")

-- Clean up any existing test users first
DELETE FROM person WHERE username IN ('applicant', 'recruiter', 'testapplicant', 'testrecruiter');

-- Insert test users with BCrypt hashed passwords
-- Password for all: password123
-- Hash verified by PasswordHashTest
INSERT INTO person (name, surname, pnr, email, password, username, role_id)
VALUES 
    ('Test', 'Applicant', '19900101-1234', 'applicant@test.com', 
     '$2a$10$NxwayRDdPlaXZfqCKNHpeO.EJ2Am/1Lrald9mac57Gn3HiHaj5gf2', 
     'applicant', 2),
    ('Test', 'Recruiter', '19850515-5678', 'recruiter@test.com',
     '$2a$10$NxwayRDdPlaXZfqCKNHpeO.EJ2Am/1Lrald9mac57Gn3HiHaj5gf2',
     'recruiter', 1);
