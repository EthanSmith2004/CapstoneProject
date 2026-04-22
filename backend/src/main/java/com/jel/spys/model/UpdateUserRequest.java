package com.jel.spys.model;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UpdateUserRequest {
    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;
}
