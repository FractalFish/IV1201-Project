package com.iv1201.recruitment.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.exception.DatabaseUnavailableException;
import com.iv1201.recruitment.repository.PersonRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business logic layer - loads user from database for authentication.
 * All methods are transactional for data consistency.
 */
@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PersonRepository personRepository;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Loads user details for authentication.
     *
     * @param username the username to look up
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Authentication attempt for username: {}", username);

    try {
        Person person = personRepository.findByUsername(username)
            .orElseThrow(() -> {
            logger.warn("Authentication failed: User not found - username: {}", username);
            return new UsernameNotFoundException("User not found: " + username);
            });
        
        logger.info("User loaded for authentication: username={}, role={}",person.getUsername(), person.getRole().getName()); 

        return new User(
            person.getUsername(),
            person.getPassword(),  // BCrypt hash from database
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + person.getRole().getName().toUpperCase())
            )
        );

    } catch (UsernameNotFoundException e) {
        // Already logged, just rethrow
        throw e;
    } catch (DataAccessException e) {
        logger.error("Database unavailable during authentication for username '{}': {}", 
                    username, e.getMessage());
        throw new DatabaseUnavailableException("Database is temporarily unavailable. Please try again later.", e);
    } catch (Exception e) {
        logger.error("Unexpected error during authentication for username '{}': {}", 
                    username, e.getMessage(), e);
        throw e;
    }
    }
}
    