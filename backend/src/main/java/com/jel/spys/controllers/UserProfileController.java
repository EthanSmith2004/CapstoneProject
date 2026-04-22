package com.jel.spys.controllers;

import com.jel.spys.facade.UserProfileFacade;
import com.jel.spys.model.CreateUserProfileRequest;
import com.jel.spys.model.UpdateUserProfileRequest;
import com.jel.spys.model.UserProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User Profile", description = "User profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserProfileController {

    @Autowired
    private UserProfileFacade userProfileFacade;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user profile", description = "Retrieve the current user's profile")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<UserProfileDTO> getUserProfile() {
        UserProfileDTO profile = userProfileFacade.getUserProfile();
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create user profile", description = "Create a new user profile")
    @ApiResponse(responseCode = "200", description = "Profile created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid profile data")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserProfileDTO> createUserProfile(@Valid @RequestBody CreateUserProfileRequest request) {
        UserProfileDTO profile = userProfileFacade.createUserProfile(request);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update user profile", description = "Update the current user's profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid profile data")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileDTO profile = userProfileFacade.updateUserProfile(request);
        return ResponseEntity.ok(profile);
    }
}
