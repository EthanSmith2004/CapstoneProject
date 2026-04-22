package com.jel.spys.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
