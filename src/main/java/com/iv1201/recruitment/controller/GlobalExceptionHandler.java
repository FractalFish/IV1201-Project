package com.iv1201.recruitment.controller;

import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application.
 * Catches exceptions and displays user-friendly error pages.
 * Never exposes stack traces to users.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles concurrent modification conflicts (optimistic locking).
     * This occurs when two recruiters try to update the same application.
     *
     * @param ex the exception
     * @param model the model for the view
     * @return the error view name
     */
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleOptimisticLockException(Exception ex, Model model) {
        logger.warn("Concurrent modification detected: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Conflict Detected");
        model.addAttribute("errorMessage", "Another user has modified this record. Please refresh and try again.");
        model.addAttribute("errorCode", 409);
        return "error";
    }

    /**
     * Handles illegal argument exceptions (validation failures).
     *
     * @param ex the exception
     * @param model the model for the view
     * @return the error view name
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        logger.warn("Invalid request: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 400);
        return "error";
    }

    /**
     * Handles illegal state exceptions (business rule violations).
     * Returns 409 Conflict since these represent state conflicts, not server errors.
     *
     * @param ex the exception
     * @param model the model for the view
     * @return the error view name
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Conflict Detected");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 409);
        return "error";
    }

    /**
     * Handles 404 not found errors from various sources.
     *
     * @param ex the exception
     * @param model the model for the view
     * @return the error view name
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(Exception ex, Model model) {
        String resourcePath = ex instanceof NoResourceFoundException 
            ? ((NoResourceFoundException) ex).getResourcePath()
            : ((NoHandlerFoundException) ex).getRequestURL();
        logger.debug("Resource not found: {}", resourcePath);
        model.addAttribute("errorTitle", "Page Not Found");
        model.addAttribute("errorMessage", "The page you're looking for doesn't exist.");
        model.addAttribute("errorCode", 404);
        return "error";
    }

    /**
     * Handles invalid URL parameters (e.g., text where a number is expected).
     * Returns 400 Bad Request for invalid input types.
     *
     * @param ex the exception
     * @param model the model for the view
     * @return the error view name
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        logger.warn("Invalid argument type for parameter '{}': expected {}, got '{}'",
                ex.getName(), ex.getRequiredType(), ex.getValue());
        model.addAttribute("errorTitle", "Invalid Input");
        model.addAttribute("errorMessage", "The provided input is not valid. Please check the URL and try again.");
        model.addAttribute("errorCode", 400);
        return "error";
    }

    /**
     * Handles all other unexpected exceptions.
     * Logs full details but shows generic message to user.
     *
     * @param ex the exception
     * @param model the model for the view
     * @return the error view name
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Unexpected error occurred", ex);
        model.addAttribute("errorTitle", "Something Went Wrong");
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorCode", 500);
        return "error";
    }
}
