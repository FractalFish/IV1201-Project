package com.iv1201.recruitment.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.repository.PersonRepository;

/**
 * Business logic layer - loads user from database for authentication
 */
@Service
public class AuthService implements UserDetailsService {

    private final PersonRepository personRepository;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
            person.getUsername(),
            person.getPassword(),  // BCrypt hash from database
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + person.getRole().getName().toUpperCase())
            )
        );
    }
}
