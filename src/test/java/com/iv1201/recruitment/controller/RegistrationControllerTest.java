package com.iv1201.recruitment.controller;

import com.iv1201.recruitment.domain.Person;
import com.iv1201.recruitment.domain.dto.RegistrationForm;
import com.iv1201.recruitment.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for RegistrationController.
 */
@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    /**
     * Verifies that registration form is displayed.
     */
    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registrationForm"));
    }

    /**
     * Verifies successful registration redirects to login with success message.
     */
    @Test
    void testSuccessfulRegistration() throws Exception {
        when(registrationService.registerApplicant(any(RegistrationForm.class)))
                .thenReturn(new Person());

        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("name", "Kalle")
                        .param("surname", "Anka")
                        .param("email", "kalle.anka@example.com")
                        .param("pnr", "19900101-1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(registrationService).registerApplicant(any(RegistrationForm.class));
    }

    /**
     * Verifies that registration failure returns to form with error.
     */
    @Test
    void testRegistrationFailure() throws Exception {
        doThrow(new IllegalArgumentException("Username already exists"))
                .when(registrationService).registerApplicant(any(RegistrationForm.class));

        mockMvc.perform(post("/register")
                        .param("username", "existinguser")
                        .param("password", "password123")
                        .param("name", "Kalle")
                        .param("surname", "Anka")
                        .param("email", "kalle.anka@example.com")
                        .param("pnr", "19900101-1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm"));
    }

    /**
     * Verifies that validation errors return to form.
     */
    @Test
    void testValidationErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasErrors("registrationForm"));

        verify(registrationService, never()).registerApplicant(any());
    }
}
