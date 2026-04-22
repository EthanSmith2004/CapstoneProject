package com.jel.spys.controllers;

import com.jel.spys.model.*;
import com.jel.spys.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User Menu", description = "User menu browsing endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserMenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/menu")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get menu", description = "Retrieve available menu items")
    @ApiResponse(responseCode = "200", description = "Menu retrieved successfully")
    public ResponseEntity<List<MenuItemDTO>> getMenu() {
        List<MenuItemDTO> menu = menuService.getMenu();
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/menu/{itemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get menu item detail", description = "Retrieve detailed information about a menu item")
    @ApiResponse(responseCode = "200", description = "Menu item retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Menu item not found")
    public ResponseEntity<MenuItemDTO> getMenuItemDetail(@PathVariable Long itemId) {
        MenuItemDTO item = menuService.getMenuItemDetail(itemId);
        return ResponseEntity.ok(item);
    }
}
