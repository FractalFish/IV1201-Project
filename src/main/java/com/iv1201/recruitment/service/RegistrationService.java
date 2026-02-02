package com.iv1201.recruitment.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.Role;
import com.iv1201.recruitment.domain.dto.RegistrationForm;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.repository.RoleRepository;

@Service
@Transactional
public class RegistrationService {
    
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;

    public RegistrationService(PasswordEncoder passwordEncoder, PersonRepository personRepository) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
    }

    public Person registerApplicant(RegistrationForm form) {
        Person person = new Person(); 
        person.setUsername(form.getUsername());

        person.setPassword(passwordEncoder.encode(form.getPassword()));

        return personRepository.save(person);
    }

}
