package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.Competence;
import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.dto.ApplicationFormDTO;
import com.iv1201.recruitment.domain.dto.ApplicationDetailsDTO;
import com.iv1201.recruitment.repository.PersonRepository;
import com.iv1201.recruitment.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Controller for applicant-specific pages.
 * Handles the applicant dashboard and application submission.
 */
@Controller
@RequestMapping("/applicant")
public class ApplicantController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicantController.class);

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
        logger.info("Dashboard requested by user: {}", username);
        Optional<Person> personOpt = personRepository.findByUsername(username);
        
        if (personOpt.isEmpty()) {
            logger.warn("Person not found for username: {}", username);
            return "redirect:/login";
        }
        
        Person person = personOpt.get();
        logger.info("Found person: {} (id={})", person.getName(), person.getPersonId());
        model.addAttribute("person", person);
        
        Optional<Application> applicationOpt = applicationService.getApplicationByPerson(person);
        logger.info("Application found: {}", applicationOpt.isPresent());
        if (applicationOpt.isPresent()) {
            Application app = applicationOpt.get();
            logger.info("Application details: id={}, status={}, createdAt={}", 
                app.getApplicationId(), app.getStatus(), app.getCreatedAt());
            model.addAttribute("appDetails", app);
            logger.info("Added application to model with applicationId={}", app.getApplicationId());
        } else {
            logger.info("No application exists - user should see 'Submit Application' button");
        }
        
        // Debug: log what's in the model
        logger.info("Model contains 'appDetails': {}", model.containsAttribute("appDetails"));
        
        return "applicant/dashboard";
    }

    /**
     * Displays the application form with competences and availability inputs.
     * Redirects to status page if user already has an application.
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
        
        // Check if already has application - redirect to status page
        if (applicationService.hasApplication(person)) {
            return "redirect:/applicant/status";
        }
        
        model.addAttribute("person", person);
        model.addAttribute("applicationForm", new ApplicationFormDTO());
        
        // Provide list of competences for the dropdown
        List<Competence> competences = applicationService.getAllCompetences();
        model.addAttribute("competences", competences);
        
        return "applicant/apply";
    }

    /**
     * Submits a new application with competences and availabilities.
     *
     * @param authentication the current user's authentication
     * @param form the application form data
     * @param bindingResult validation results
     * @param redirectAttributes for flash messages
     * @param model the model for the view
     * @return redirect to status or back to form on error
     */
    @PostMapping("/apply")
    public String submitApplication(Authentication authentication,
                                    @Valid @ModelAttribute("applicationForm") ApplicationFormDTO form,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        String username = authentication.getName();
        Optional<Person> personOpt = personRepository.findByUsername(username);
        
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Person person = personOpt.get();
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("person", person);
            model.addAttribute("competences", applicationService.getAllCompetences());
            return "applicant/apply";
        }
        
        try {
            applicationService.submitApplication(person, form);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/applicant/status";
        } catch (IllegalArgumentException e) {
            model.addAttribute("person", person);
            model.addAttribute("competences", applicationService.getAllCompetences());
            model.addAttribute("error", e.getMessage());
            return "applicant/apply";
        }
    }

    /**
     * Displays the application status page with full details.
     *
     * @param authentication the current user's authentication
     * @param model the model for the view
     * @return the status view or redirect if no application
     */
    @GetMapping("/status")
    public String showStatus(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Person> personOpt = personRepository.findByUsername(username);
        
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Person person = personOpt.get();
        model.addAttribute("person", person);
        
        Optional<Application> applicationOpt = applicationService.getApplicationByPerson(person);
        
        if (applicationOpt.isEmpty()) {
            return "redirect:/applicant/apply";
        }
        
        // Get full application details with competences and availabilities
        Optional<ApplicationDetailsDTO> detailsOpt = applicationService
                .getApplicationDetails(applicationOpt.get().getApplicationId());
        
        detailsOpt.ifPresent(details -> model.addAttribute("applicationDetails", details));
        model.addAttribute("appDetails", applicationOpt.get());
        
        return "applicant/status";
    }
}
