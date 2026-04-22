package com.jel.spys.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class GenericManagementController<T, ID> {

    private final com.jel.spys.service.GenericManagementService<T, ID> service;

    public GenericManagementController(com.jel.spys.service.GenericManagementService<T, ID> service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<T> create(@RequestBody T entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a resource by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource found"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<T> findById(@PathVariable ID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Find all resources")
    @ApiResponse(responseCode = "200", description = "Resources found")
    public ResponseEntity<List<T>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T entityDetails) {
        return ResponseEntity.ok(service.update(id, entityDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
