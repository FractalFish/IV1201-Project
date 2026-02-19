package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.dto.RegistrationForm;
import com.iv1201.recruitment.exception.EmailAlreadyTakenException;
import com.iv1201.recruitment.exception.UsernameAlreadyTakenException;
import com.iv1201.recruitment.service.RegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling user registration.
 * Provides endpoints for displaying and processing the registration form.
 */
@Controller
public class RegistrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    
    private final RegistrationService registrationService;
    
    /**
     * Constructs a RegistrationController with required dependencies.
     *
     * @param registrationService service for registration operations
     */
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    
    /**
     * Displays the registration form.
     *
     * @param model the model to add attributes to
     * @return the registration view name
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }
    
    /**
     * Processes the registration form submission.
     *
     * @param form the submitted registration form
     * @param bindingResult validation results
     * @param redirectAttributes attributes for redirect
     * @return redirect to login on success, or registration view on failure
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.debug("Registration form has validation errors: {}", bindingResult.getAllErrors());
            return "register";
        }
        
        try {
            registrationService.registerApplicant(form);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        }
        catch (UsernameAlreadyTakenException e) {
            logger.debug("Registration failed: username already taken");
            bindingResult.rejectValue("username", "error.username.taken");
            return "register";
        }
        catch (EmailAlreadyTakenException e) {
            logger.debug("Registration failed: email already taken");
            bindingResult.rejectValue("email", "error.email.taken");
            return "register";
        }
        catch (DataIntegrityViolationException e) {
            logger.debug("Registration failed due to data integrity: {}", e.getMessage());
            bindingResult.reject("error.registration.failed");
            return "register";
        }
        catch (Exception e) {
            logger.error("Unexpected registration error for user '{}': {}", form.getUsername(), e.getMessage(), e);
            bindingResult.reject("error.registration.failed");
            return "register";
        }
    }
}