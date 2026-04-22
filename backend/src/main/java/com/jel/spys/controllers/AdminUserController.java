package com.jel.spys.controllers;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.AdminCreateUserRequest;
import com.jel.spys.model.AdminUpdateUserRequest;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin User Management", description = "Admin user management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') && hasRole('USER_ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - User admin role required")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN') && hasRole('USER_ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user with admin privileges")
    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserEntity.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Access denied - User admin role required")
    @ApiResponse(responseCode = "409", description = "Email already exists")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody AdminCreateUserRequest createRequest) {
        UserEntity currentAdmin = userService.getCurrentUser();
        UserEntity user = userService.createUser(createRequest);
        
        // Log admin action
        userEventService.logEvent(currentAdmin, UserEventType.ADMIN_USER_CREATED);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('USER_ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(schema = @Schema(implementation = UserEntity.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied - User admin role required")
    public ResponseEntity<UserEntity> getUserById(@Parameter(description = "User ID") @PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('USER_ADMIN')")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied - User admin role required")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        UserEntity currentAdmin = userService.getCurrentUser();
        userService.deleteUser(id);
        
        // Log admin action
        userEventService.logEvent(currentAdmin, UserEventType.ADMIN_USER_DELETED);
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('USER_ADMIN')")
    @Operation(summary = "Update user", description = "Update a user by their ID with admin privileges")
    @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserEntity.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied - User admin role required")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    public ResponseEntity<UserEntity> updateUser(@Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest updateRequest) {
        UserEntity currentAdmin = userService.getCurrentUser();
        UserEntity user = userService.updateUser(id, updateRequest);
        
        // Log admin action
        userEventService.logEvent(currentAdmin, UserEventType.ADMIN_USER_UPDATED);
        
        return ResponseEntity.ok(user);
    }
}
