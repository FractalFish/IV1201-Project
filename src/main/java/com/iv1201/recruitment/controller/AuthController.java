package com.iv1201.recruitment.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for authentication and home page routing.
 * Routes users to appropriate dashboards based on their role.
 */
@Controller
public class AuthController {
    

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    /**
     * Displays the login page.
     *
     * @return the login view name
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * Routes authenticated users to their role-specific dashboard.
     * Recruiters go to /recruiter/dashboard, applicants go to /applicant/dashboard.
     *
     * @param authentication the current user's authentication
     * @return redirect to the appropriate dashboard
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            logger.debug("Unauthenticated user accessing home - redirecting to login"); 
            return "redirect:/login";
        }
        String username = authentication.getName();
        
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RECRUITER"))) {
            logger.debug("User '{}' with ROLE_RECRUITER accessing home - redirecting to recruiter dashboard", username);
            return "redirect:/recruiter/dashboard";
        }
        
        logger.debug("User '{}' with ROLE_APPLICANT accessing home - redirecting to applicant dashboard", username);
        return "redirect:/applicant/dashboard";
    }
}
