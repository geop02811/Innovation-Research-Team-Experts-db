package org.innov.expertdb.auth.dtos.register;

import org.innov.expertdb.user.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(

        @NotBlank(message="Surname is required")
        String name,

        @NotBlank(message="Surname is required")
        String surname, 
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        String password, 
        
        @NotNull(message = "Role is required")
        Role role) { }
