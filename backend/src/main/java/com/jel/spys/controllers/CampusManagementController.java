package com.jel.spys.controllers;

import com.jel.spys.entity.CampusEntity;
import com.jel.spys.service.CampusManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/campuses")
@Tag(name = "Admin - Campus Management", description = "Endpoints for managing campuses")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class CampusManagementController extends GenericManagementController<CampusEntity, Long> {
    public CampusManagementController(CampusManagementService service) {
        super(service);
    }
}
