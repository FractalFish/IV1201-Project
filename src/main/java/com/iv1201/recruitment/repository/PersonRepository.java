package com.iv1201.recruitment.repository; 

import com.iv1201.recruitment.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    
    Person findByUsername(String username);
}