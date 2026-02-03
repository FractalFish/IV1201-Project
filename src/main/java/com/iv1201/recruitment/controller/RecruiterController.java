package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.Application;
import com.iv1201.recruitment.domain.ApplicationStatus;
import com.iv1201.recruitment.service.ApplicationService;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for recruiter-specific pages.
 * Handles the recruiter dashboard and application management views.
 */
@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    private final ApplicationService applicationService;

    public RecruiterController(ApplicationService applicationService) {
        this.applicationService = applicationService;
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
        ApplicationStatus filterStatus = null;
        
        if (status != null && !status.isEmpty()) {
            try {
                filterStatus = ApplicationStatus.valueOf(status.toUpperCase());
                model.addAttribute("currentFilter", status);
            } catch (IllegalArgumentException e) {
                // Invalid status, show all
            }
        }
        
        applications = applicationService.getApplications(filterStatus);
        model.addAttribute("applications", applications);
        model.addAttribute("statuses", ApplicationStatus.values());
        
        return "recruiter/dashboard";
    }

    /**
     * Displays the application detail page.
     *
     * @param id the application ID
     * @param model the model for the view
     * @return the application detail view or redirect if not found
     */
    @GetMapping("/application/{id}")
    public String viewApplication(@PathVariable("id") Integer id, Model model) {
        Optional<Application> applicationOpt = applicationService.getApplicationById(id);
        
        if (applicationOpt.isEmpty()) {
            return "redirect:/recruiter/dashboard";
        }
        
        model.addAttribute("application", applicationOpt.get());
        model.addAttribute("statuses", ApplicationStatus.values());
        
        return "recruiter/application-detail";
    }

    /**
     * Updates the status of an application.
     * Handles optimistic locking exceptions for concurrent modifications.
     *
     * @param id the application ID
     * @param status the new status
     * @param redirectAttributes for flash messages
     * @return redirect to application detail
     */
    @PostMapping("/application/{id}/status")
    public String updateStatus(@PathVariable("id") Integer id,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes) {
        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            applicationService.updateStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (ObjectOptimisticLockingFailureException e) {
            redirectAttributes.addFlashAttribute("conflict", true);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/recruiter/application/" + id;
    }
}
