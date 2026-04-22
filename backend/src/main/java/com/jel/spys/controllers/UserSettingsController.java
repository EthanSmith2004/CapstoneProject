package com.jel.spys.controllers;

import com.jel.spys.facade.UserSettingsFacade;
import com.jel.spys.model.UpdateUserSettingsRequest;
import com.jel.spys.model.UserSettingsDTO;
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
@Tag(name = "User Settings", description = "User settings and preferences endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserSettingsController {

    @Autowired
    private UserSettingsFacade userSettingsFacade;

    @GetMapping("/settings")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user settings", description = "Retrieve current user's settings and preferences")
    @ApiResponse(responseCode = "200", description = "Settings retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User or settings not found")
    public ResponseEntity<UserSettingsDTO> getUserSettings() {
        UserSettingsDTO settings = userSettingsFacade.getUserSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update user settings", description = "Update current user's settings and preferences")
    @ApiResponse(responseCode = "200", description = "Settings updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid settings data")
    @ApiResponse(responseCode = "404", description = "User or settings not found")
    public ResponseEntity<UserSettingsDTO> updateUserSettings(
            @Valid @RequestBody UpdateUserSettingsRequest request) {
        UserSettingsDTO settings = userSettingsFacade.updateUserSettings(request);
        return ResponseEntity.ok(settings);
    }
}
