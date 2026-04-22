package com.jel.spys.controllers;

import com.jel.spys.model.SelectDTO;
import com.jel.spys.service.ListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/list")
@RequiredArgsConstructor
@Tag(name = "List", description = "Endpoints for retrieving lists of data")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'FINANCIAL_ADMIN', 'MENU_ADMIN', 'USER_ADMIN', 'AUDIT_ADMIN')")
public class ListController {

    private final ListService listService;

    @GetMapping("/allergies")
    @Operation(summary = "Get all allergy names")
    @ApiResponse(responseCode = "200", description = "Allergy names found")
    public ResponseEntity<List<SelectDTO>> getAllergyNames() {
        return ResponseEntity.ok(listService.getAllergyNames());
    }

    @GetMapping("/campuses")
    @Operation(summary = "Get all campus names")
    @ApiResponse(responseCode = "200", description = "Campus names found")
    public ResponseEntity<List<SelectDTO>> getCampusNames() {
        return ResponseEntity.ok(listService.getCampusNames());
    }

    @GetMapping("/residences")
    @Operation(summary = "Get all residence names")
    @ApiResponse(responseCode = "200", description = "Residence names found")
    public ResponseEntity<List<SelectDTO>> getResidenceNames() {
        return ResponseEntity.ok(listService.getResidenceNames());
    }
}
