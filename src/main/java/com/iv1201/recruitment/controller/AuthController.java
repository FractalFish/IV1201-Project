package com.iv1201.recruitment.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for authentication and home page routing.
 * Routes users to appropriate dashboards based on their role.
 */
@Controller
public class AuthController {
    
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
            return "redirect:/login";
        }
        
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RECRUITER"))) {
            return "redirect:/recruiter/dashboard";
        }
        
        return "redirect:/applicant/dashboard";
    }
}
