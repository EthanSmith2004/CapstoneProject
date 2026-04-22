package com.jel.spys.controllers;

import com.jel.spys.entity.AllergyEntity;
import com.jel.spys.service.AllergyManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/allergies")
@Tag(name = "Admin - Allergy Management", description = "Endpoints for managing allergies")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AllergyManagementController extends GenericManagementController<AllergyEntity, Long> {
    public AllergyManagementController(AllergyManagementService service) {
        super(service);
    }
}
