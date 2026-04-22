package com.jel.spys.controllers;

import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.*;
import com.jel.spys.service.MenuService;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Menu", description = "Admin menu management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminMenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @GetMapping("/menu")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get paginated menu items (DEPRECATED)", description = "Retrieve menu items with pagination. Use /menu/items, /menu/current, or /menu/historic instead.")
    @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    @Deprecated
    public ResponseEntity<List<MenuItemDTO>> getMenuPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<MenuItemDTO> items = menuService.getMenuItemsAdminPaginated(page, size);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/menu/items")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get draft menu items", description = "Retrieve all menu items with null delivery, release, edit and order dates")
    @ApiResponse(responseCode = "200", description = "Draft menu items retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<MenuItemDTO>> getDraftMenuItems() {
        List<MenuItemDTO> items = menuService.getDraftMenuItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/menu/current")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get current menu items", description = "Retrieve all released items with delivery, release, edit, and order dates where orderBy is not past")
    @ApiResponse(responseCode = "200", description = "Current menu items retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<MenuItemDTO>> getCurrentMenuItems() {
        List<MenuItemDTO> items = menuService.getCurrentMenuItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/menu/historic")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get historic menu items", description = "Retrieve all items where orderBy date is in the past")
    @ApiResponse(responseCode = "200", description = "Historic menu items retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<MenuItemDTO>> getHistoricMenuItems() {
        List<MenuItemDTO> items = menuService.getHistoricMenuItems();
        return ResponseEntity.ok(items);
    }
    
    @PostMapping("/menu/queue")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Queue a menu item", description = "Create a new menu item by copying an existing one with new dates")
    @ApiResponse(responseCode = "200", description = "Menu item queued successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Source menu item not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<MenuItemDTO> queueMenuItem(
            @Valid @RequestBody MenuItemQueueRequest request) {
        MenuItemDTO item = menuService.queueMenuItem(request);
        return ResponseEntity.ok(item);
    }
    
    @GetMapping("/menu/stats")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Get menu item statistics", description = "Get statistics about popular items from order history")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<MenuItemStatisticsDTO>> getMenuItemStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        
        List<MenuItemStatisticsDTO> stats;
        if (startDate != null && endDate != null) {
            stats = menuService.getMenuItemStatisticsByDateRange(startDate, endDate);
        } else {
            stats = menuService.getMenuItemStatistics();
        }
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/menu/search")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Search menu items", description = "Search menu items with pagination")
    @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<List<MenuItemDTO>> searchMenuPaginated(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<MenuItemDTO> items = menuService.searchMenuItemsPaginated(search, page, size);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/menu")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Create menu item", description = "Create a new menu item")
    @ApiResponse(responseCode = "200", description = "Menu item created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid menu item data")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @Valid @RequestBody AdminMenuItemCreateRequest request) {
        MenuItemDTO item = menuService.createMenuItem(request);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_MENU_CREATED);
        
        return ResponseEntity.ok(item);
    }

    @PutMapping("/menu/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Update menu item", description = "Update an existing menu item")
    @ApiResponse(responseCode = "200", description = "Menu item updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid menu item data")
    @ApiResponse(responseCode = "404", description = "Menu item not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody AdminMenuItemUpdateRequest request) {
        MenuItemDTO item = menuService.updateMenuItem(id, request);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_MENU_UPDATED);
        
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/menu/{id}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('MENU_ADMIN')")
    @Operation(summary = "Delete menu item", description = "Delete a menu item by ID. Only draft and current (non-historic) items can be deleted.")
    @ApiResponse(responseCode = "204", description = "Menu item deleted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Menu admin role required")
    @ApiResponse(responseCode = "404", description = "Menu item not found")
    @ApiResponse(responseCode = "400", description = "Cannot delete historic menu item")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        log.info("Deleting menu item with ID: {}", id);
        menuService.deleteMenuItem(id);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_MENU_DELETED);
        
        return ResponseEntity.noContent().build();
    }
}
