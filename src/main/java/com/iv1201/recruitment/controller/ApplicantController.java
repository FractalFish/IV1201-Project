package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.repository.ApplicationRepository;
import com.iv1201.recruitment.repository.PersonRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Controller for applicant-specific pages.
 * Handles the applicant dashboard and application-related views.
 */
@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    private final PersonRepository personRepository;
    private final ApplicationRepository applicationRepository;

    public ApplicantController(PersonRepository personRepository,
                               ApplicationRepository applicationRepository) {
        this.personRepository = personRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Displays the applicant dashboard.
     * Shows the current application status if one exists.
     *
     * @param authentication the current user's authentication
     * @param model the model for the view
     * @return the applicant dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Person> personOpt = personRepository.findByUsername(username);
        
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Person person = personOpt.get();
        model.addAttribute("person", person);
        
        Optional<Application> applicationOpt = applicationRepository.findByPerson(person);
        applicationOpt.ifPresent(app -> model.addAttribute("application", app));
        
        return "applicant/dashboard";
    }
}
