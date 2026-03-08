package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.exception.UsernameAlreadyTakenException;
import com.iv1201.recruitment.service.EmailService;
import com.iv1201.recruitment.service.EmailVerificationService;
import com.iv1201.recruitment.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountClaimingController {

    private static final Logger logger = LoggerFactory.getLogger(AccountClaimingController.class);

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
            logger.warn("Claim attempt with blank email");
            model.addAttribute("error", "Email is required");
            return "claim";
        }

        if (!registrationService.isLegacyUser(email)) {
            logger.warn("Claim attempt for non-legacy email: {}", email);
            model.addAttribute("error", "No pending application found for this email");
            return "claim";
        }

        String token = verificationService.generateToken(email);
        emailService.sendVerificationEmail(email, token);
        
        logger.info("Verification email sent for claim: email={}", email);
        model.addAttribute("success", "Verification email sent. Check console for link.");
        return "claim";
    }

    @GetMapping("/verify")
    public String showVerifyForm(@RequestParam String token, Model model) {
        if (verificationService.validateToken(token).isEmpty()) {
            logger.warn("Verify form accessed with invalid or expired token");
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
            logger.warn("Verify submission with invalid or expired token");
            model.addAttribute("error", "Invalid or expired token");
            model.addAttribute("token", token);
            return "verify";
        }
        
        String email = verification.get().getEmail();

        try {
            registrationService.completeLegacyRegistration(email, username, password);
        } catch (UsernameAlreadyTakenException e) {
            logger.warn("Legacy registration failed: username '{}' already taken for email={}", username, email);
            model.addAttribute("error", "Username is already taken");
            model.addAttribute("token", token);
            return "verify";
        }

        verificationService.markAsUsed(token);
        
        logger.info("Legacy account claimed successfully: email={}, username={}", email, username);
        return "redirect:/login?claimed";
    }
}
