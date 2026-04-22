package com.jel.spys.controllers;

import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.*;
import com.jel.spys.service.MenuTemplateService;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu-templates")
@Slf4j
@Tag(name = "Admin Menu Templates", description = "Admin menu template management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminMenuTemplateController {

    @Autowired
    private MenuTemplateService menuTemplateService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @GetMapping("/presets")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get distinct preset names", description = "Retrieve all distinct preset names with metadata")
    @ApiResponse(responseCode = "200", description = "Preset names retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<PresetNameDTO>> getDistinctPresetNames() {
        log.info("Getting distinct preset names");
        List<PresetNameDTO> presets = menuTemplateService.getDistinctPresetNames();
        return ResponseEntity.ok(presets);
    }

    @GetMapping("/preset/{presetName}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get templates by preset name", description = "Retrieve all templates for a specific preset name")
    @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<MenuTemplateDTO>> getTemplatesByPresetName(
            @PathVariable String presetName) {
        log.info("Getting templates for preset: {}", presetName);
        List<MenuTemplateDTO> templates = menuTemplateService.getTemplatesByPresetName(presetName);
        return ResponseEntity.ok(templates);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Create new template", description = "Create a new menu template")
    @ApiResponse(responseCode = "200", description = "Template created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<MenuTemplateDTO> createTemplate(
            @Valid @RequestBody MenuTemplateCreateRequest request) {
        log.info("Creating new template for preset: {}", request.getPresetName());
        MenuTemplateDTO template = menuTemplateService.createTemplate(request);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_MENU_TEMPLATE_CREATED);
        
        return ResponseEntity.ok(template);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Update existing template", description = "Update an existing menu template")
    @ApiResponse(responseCode = "200", description = "Template updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<MenuTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody MenuTemplateUpdateRequest request) {
        log.info("Updating template with ID: {}", id);
        MenuTemplateDTO template = menuTemplateService.updateTemplate(id, request);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_MENU_TEMPLATE_UPDATED);
        
        return ResponseEntity.ok(template);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Delete template", description = "Delete a menu template by ID")
    @ApiResponse(responseCode = "204", description = "Template deleted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    @ApiResponse(responseCode = "404", description = "Template not found")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        log.info("Deleting template with ID: {}", id);
        menuTemplateService.deleteTemplate(id);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_MENU_TEMPLATE_DELETED);
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/preset/{presetName}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Batch delete templates by preset", description = "Delete all templates for a specific preset name")
    @ApiResponse(responseCode = "204", description = "Templates deleted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<Void> deleteTemplatesByPresetName(@PathVariable String presetName) {
        log.info("Deleting all templates for preset: {}", presetName);
        menuTemplateService.deleteTemplatesByPresetName(presetName);
        return ResponseEntity.noContent().build();
    }
}