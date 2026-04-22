package com.jel.spys.controllers;

import com.jel.spys.entity.ResidenceEntity;
import com.jel.spys.service.ResidenceManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/residences")
@Tag(name = "Admin - Residence Management", description = "Endpoints for managing residences")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class ResidenceManagementController extends GenericManagementController<ResidenceEntity, Long> {
    public ResidenceManagementController(ResidenceManagementService service) {
        super(service);
    }
}
