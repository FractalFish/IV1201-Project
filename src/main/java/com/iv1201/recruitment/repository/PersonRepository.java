package com.iv1201.recruitment.repository;

import com.iv1201.recruitment.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Person entity operations.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    /**
     * Find a person by their username.
     * @param username the username to search for
     * @return Optional containing the person if found
     */
    Optional<Person> findByUsername(String username);

    /**
     * Find a person by their email.
     * @param email the email to search for
     * @return Optional containing the person if found
     */
    Optional<Person> findByEmail(String email);

    /**
     * Check if a username already exists.
     * @param username the username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email already exists.
     * @param email the email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);
}