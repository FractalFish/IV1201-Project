package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.ApplicationStatus;
import com.iv1201.recruitment.repository.ApplicationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for recruiter-specific pages.
 * Handles the recruiter dashboard and application management views.
 */
@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    private final ApplicationRepository applicationRepository;

    public RecruiterController(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Displays the recruiter dashboard with a list of applications.
     * Supports filtering by application status.
     *
     * @param status optional filter for application status
     * @param model the model for the view
     * @return the recruiter dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String status, Model model) {
        List<Application> applications;
        
        if (status != null && !status.isEmpty()) {
            try {
                ApplicationStatus filterStatus = ApplicationStatus.valueOf(status.toUpperCase());
                applications = applicationRepository.findByStatus(filterStatus);
                model.addAttribute("currentFilter", status);
            } catch (IllegalArgumentException e) {
                applications = applicationRepository.findAllByOrderByCreatedAtDesc();
            }
        } else {
            applications = applicationRepository.findAllByOrderByCreatedAtDesc();
        }
        
        model.addAttribute("applications", applications);
        model.addAttribute("statuses", ApplicationStatus.values());
        
        return "recruiter/dashboard";
    }
}
