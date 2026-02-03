package com.iv1201.recruitment.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration.
 * Contains all fields required to register a new applicant.
 */
public class RegistrationForm {
    
    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 50, message = "{validation.username.size}")
    private String username;
    
    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, max = 100, message = "{validation.password.size}")
    private String password;
    
    @NotBlank(message = "{validation.name.required}")
    @Size(max = 255, message = "{validation.name.size}")
    private String name;
    
    @NotBlank(message = "{validation.surname.required}")
    @Size(max = 255, message = "{validation.surname.size}")
    private String surname;
    
    @Pattern(regexp = "^(\\d{8}-\\d{4})?$", message = "{validation.pnr.format}")
    private String pnr;
    
    @Email(message = "{validation.email.format}")
    private String email;

    public RegistrationForm() {}

    // Getters and Setters
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getSurname() { 
        return surname; 
    }
    
    public void setSurname(String surname) { 
        this.surname = surname; 
    }

    public String getPnr() { 
        return pnr; 
    }
    
    public void setPnr(String pnr) { 
        this.pnr = pnr; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
}
