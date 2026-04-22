package com.jel.spys.controllers;

import com.jel.spys.model.AdminCreateReportRequest;
import com.jel.spys.model.ReportStatusDTO;
import com.jel.spys.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Reports", description = "Admin report generation and management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/reports")
    @PreAuthorize("hasRole('ADMIN') && hasAnyRole('FINANCIAL_ADMIN', 'MENU_ADMIN', 'USER_ADMIN', 'AUDIT_ADMIN')")
    @Operation(summary = "Create new report", description = "Request generation of a new report")
    @ApiResponse(responseCode = "200", description = "Report creation request submitted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid report request")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<ReportStatusDTO> createReport(@Valid @RequestBody AdminCreateReportRequest request) {
        ReportStatusDTO response =  reportService.createReport(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN') && hasAnyRole('FINANCIAL_ADMIN', 'MENU_ADMIN', 'USER_ADMIN', 'AUDIT_ADMIN')")
    @Operation(summary = "Get report status", description = "Check the status of a report generation request")
    @ApiResponse(responseCode = "200", description = "Report status retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<ReportStatusDTO> getReportStatus(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {
        ReportStatusDTO response =  reportService.getReportStatus(reportId);
        return ResponseEntity.ok(response);
    }
}
