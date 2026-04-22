package com.jel.spys.controllers;

import com.jel.spys.model.AuthResponse;
import com.jel.spys.model.LoginRequest;
import com.jel.spys.model.RefreshTokenRequest;
import com.jel.spys.model.RegisterRequest;
import com.jel.spys.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {@Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticate a user with username and password")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully", 
                content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    @ApiResponse(responseCode = "200", description = "User registered successfully", 
                content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "User already exists or invalid data")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        AuthResponse authResponse = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refresh an expired access token using a refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully", 
                content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout a user by invalidating their refresh token")
    @ApiResponse(responseCode = "200", description = "User logged out successfully")
    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}
