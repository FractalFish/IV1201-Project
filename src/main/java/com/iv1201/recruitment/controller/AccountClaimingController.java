package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.dto.RegistrationForm;
import com.iv1201.recruitment.service.EmailService;
import com.iv1201.recruitment.service.EmailVerificationService;
import com.iv1201.recruitment.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountClaimingController {

    private final EmailVerificationService verificationService;
    private final RegistrationService registrationService;
    private final EmailService emailService;

    public AccountClaimingController(EmailVerificationService verificationService,
                                    RegistrationService registrationService,
                                    EmailService emailService) {
        this.verificationService = verificationService;
        this.registrationService = registrationService;
        this.emailService = emailService;
    }

    @GetMapping("/claim")
    public String showClaimForm() {
        return "claim";
    }

    @PostMapping("/claim")
    public String submitClaim(@RequestParam String email, Model model) {
        if (email == null || email.isBlank()) {
            model.addAttribute("error", "Email is required");
            return "claim";
        }

        if (!registrationService.isLegacyUser(email)) {
            model.addAttribute("error", "No pending application found for this email");
            return "claim";
        }

        String token = verificationService.generateToken(email);
        emailService.sendVerificationEmail(email, token);
        
        model.addAttribute("success", "Verification email sent. Check console for link.");
        return "claim";
    }

    @GetMapping("/verify")
    public String showVerifyForm(@RequestParam String token, Model model) {
        if (verificationService.validateToken(token).isEmpty()) {
            model.addAttribute("error", "Invalid or expired token");
            return "claim";
        }
        
        model.addAttribute("token", token);
        return "verify";
    }

    @PostMapping("/verify")
    public String submitVerify(@RequestParam String token,
                              @RequestParam String password,
                              @RequestParam String username,
                              Model model) {
        var verification = verificationService.validateToken(token);
        
        if (verification.isEmpty()) {
            model.addAttribute("error", "Invalid or expired token");
            model.addAttribute("token", token);
            return "verify";
        }
        
        String email = verification.get().getEmail();
        
        registrationService.completeLegacyRegistration(email, username, password);
        verificationService.markAsUsed(token);
        
        return "redirect:/login?claimed";
    }
}
