package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.service.ApplicationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller for applicant-specific pages.
 * Handles the applicant dashboard and application submission.
 */
@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    private final PersonRepository personRepository;
    private final ApplicationService applicationService;

    public ApplicantController(PersonRepository personRepository,
                               ApplicationService applicationService) {
        this.personRepository = personRepository;
        this.applicationService = applicationService;
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
        
        Optional<Application> applicationOpt = applicationService.getApplicationByPerson(person);
        applicationOpt.ifPresent(app -> model.addAttribute("application", app));
        
        return "applicant/dashboard";
    }

    /**
     * Displays the application form.
     * Redirects to dashboard if user already has an application.
     *
     * @param authentication the current user's authentication
     * @param model the model for the view
     * @return the application form view or redirect
     */
    @GetMapping("/apply")
    public String showApplyForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Person> personOpt = personRepository.findByUsername(username);
        
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Person person = personOpt.get();
        
        // Check if already has application
        if (applicationService.hasApplication(person)) {
            return "redirect:/applicant/dashboard";
        }
        
        model.addAttribute("person", person);
        return "applicant/apply";
    }

    /**
     * Submits a new application.
     *
     * @param authentication the current user's authentication
     * @param redirectAttributes for flash messages
     * @return redirect to dashboard
     */
    @PostMapping("/apply")
    public String submitApplication(Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Optional<Person> personOpt = personRepository.findByUsername(username);
        
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Person person = personOpt.get();
        
        try {
            applicationService.createApplication(person);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "application.already.exists");
        }
        
        return "redirect:/applicant/dashboard";
    }
}
