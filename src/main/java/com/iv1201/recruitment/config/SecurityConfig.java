package com.iv1201.recruitment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.iv1201.recruitment.exception.DatabaseUnavailableException;
import com.iv1201.recruitment.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security configuration - enables login with BCrypt password hashing
 * and role-based access control.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final AuthService authService;
    
    public SecurityConfig(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Helper method to check if any cause in the exception chain is a database-related exception.
     *
     * @param exception the exception to check
     * @return true if a database error is found in the cause chain
     */
    private boolean hasDatabaseErrorInCause(Throwable exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof DatabaseUnavailableException ||
                cause instanceof org.springframework.dao.DataAccessException ||
                cause instanceof org.springframework.transaction.CannotCreateTransactionException ||
                cause instanceof org.springframework.transaction.TransactionSystemException ||
                cause instanceof org.springframework.security.authentication.InternalAuthenticationServiceException ||
                cause instanceof java.sql.SQLTransientConnectionException ||
                cause instanceof java.sql.SQLNonTransientConnectionException ||
                cause instanceof java.sql.SQLException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/error").permitAll()
                .requestMatchers("/recruiter/**").hasRole("RECRUITER")
                .requestMatchers("/applicant/**").hasRole("APPLICANT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                // success handler to log successful logins
                .successHandler((request, response, authentication) -> {
                    logger.info("Login successful: user={}, role={}", 
                        authentication.getName(), 
                        authentication.getAuthorities());
                    response.sendRedirect("/");
                })
                // failure handler to log failed login attempts and distinguish error types
                .failureHandler((request, response, exception) -> {
                    String username = request.getParameter("username");
                    
                    // Check for database errors in the exception chain
                    boolean isDatabaseError = exception instanceof DatabaseUnavailableException ||
                            exception instanceof org.springframework.security.authentication.InternalAuthenticationServiceException ||
                            hasDatabaseErrorInCause(exception);
                    
                    if (isDatabaseError) {
                        logger.error("Login failed due to database unavailability: username={}", 
                            username != null ? username : "unknown");
                        response.sendRedirect("/login?dbError");
                    } else {
                        logger.warn("Login failed: username={}, reason={}", 
                            username != null ? username : "unknown", 
                            exception.getMessage());
                        response.sendRedirect("/login?error");
                    }
                })
                .permitAll()
            )
            // logout handler to log successful logouts
            .logout(logout -> logout
                .logoutSuccessHandler((request, response, authentication) -> {
                    if (authentication != null) {
                        logger.info("User logged out: {}", authentication.getName());
                    }
                    response.sendRedirect("/login?logout");
                })
                .permitAll()
            )
            // Access denied handler to redirect and log unauthorized access attempts
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    
                    // if user is authenticated but access is denied then it is redirected to their correct dashboard
                    if (auth != null && auth.isAuthenticated()) {
                        String username = auth.getName();
                        logger.warn("Access denied: user={}, attempted_url={}", 
                            username, request.getRequestURI());
                        
                        // Redirect to appropriate dashboard based on role
                        boolean isRecruiter = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_RECRUITER"));
                        // if rectruiter --> redirect to rectruiter dashboard 
                        if (isRecruiter) {
                            logger.debug("Redirecting recruiter '{}' to recruiter dashboard", username);
                            response.sendRedirect("/recruiter/dashboard");
                            // if applicant --> redirect to applicant dashboard 
                        } else {
                            logger.debug("Redirecting applicant '{}' to applicant dashboard", username);
                            response.sendRedirect("/applicant/dashboard");
                        }
                        // // if user is not authenticated/annonymus --> redirect to login page 
                    } else {
                        logger.warn("Access denied: user=anonymous, attempted_url={}", 
                            request.getRequestURI());
                        response.sendRedirect("/login?error");
                    }
                })
            );
        
        return http.build();
    }
}
