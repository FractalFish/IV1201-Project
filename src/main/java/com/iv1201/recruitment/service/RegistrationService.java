package com.iv1201.recruitment.service;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import com.iv1201.recruitment.domain.dto.RegistrationForm;
import com.iv1201.recruitment.exception.EmailAlreadyTakenException;
import com.iv1201.recruitment.exception.UsernameAlreadyTakenException;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Service for handling user registration.
 * Creates new applicant accounts with hashed passwords.
 */
@Service
public class RegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private static final String APPLICANT_ROLE = "applicant";
    
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Constructs a RegistrationService with required dependencies.
     *
     * @param personRepository repository for person entities
     * @param roleRepository repository for role entities
     * @param passwordEncoder encoder for hashing passwords
     */
    public RegistrationService(PersonRepository personRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Registers a new applicant user.
     *
     * @param form the registration form data
     * @return the created Person entity
     * @throws IllegalArgumentException if username or email already exists
     * @throws IllegalStateException if applicant role is not found
     */
    @Transactional
    public Person registerApplicant(RegistrationForm form) {

        logger.info("Registration attempt for username: {}, email: {}", form.getUsername(), form.getEmail());    
        
    try {


        if (personRepository.existsByUsername(form.getUsername())) {
            logger.warn("Registration failed: username '{}' already taken", form.getUsername());
            throw new UsernameAlreadyTakenException(form.getUsername());
        }

        if (form.getEmail() != null && !form.getEmail().isBlank()
            && personRepository.existsByEmail(form.getEmail())) {
            logger.warn("Registration failed: email '{}' already taken", form.getEmail());
            throw new EmailAlreadyTakenException(form.getEmail());
        }


    
        
        Role applicantRole = roleRepository.findByName(APPLICANT_ROLE);
        if  (applicantRole == null) {
            logger.error("System configuration error: applicant role '{}' not found in database", APPLICANT_ROLE);
            throw new IllegalStateException("Applicant role not found in database");
        }
        

        logger.debug("Creating new person for username: {}", form.getUsername());
        Person person = new Person();
        person.setUsername(form.getUsername());
        person.setPassword(passwordEncoder.encode(form.getPassword()));
        person.setName(form.getName());
        person.setSurname(form.getSurname());
        person.setPnr(form.getPnr());
        person.setEmail(form.getEmail());
        person.setRole(applicantRole);
        
        Person savedPerson = personRepository.save(person);
        logger.info("Successfully registered new applicant: username={}, personId={}", 
                savedPerson.getUsername(), savedPerson.getPersonId());
        return savedPerson;
        } 
        catch (IllegalArgumentException e) {
            throw e;
        } 
        catch (Exception e) {
            logger.error("Unexpected error during registration for username '{}': {}", form.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Checks if a username is already taken.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return personRepository.existsByUsername(username);
    }
    
    /**
     * Checks if an email is already registered.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailTaken(String email) {
        return email != null && !email.isBlank() && personRepository.existsByEmail(email);
    }
    
}
