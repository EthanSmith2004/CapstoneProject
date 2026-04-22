package com.jel.spys.controllers;

import com.jel.spys.model.*;
import com.jel.spys.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Audit", description = "Admin audit and logging endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminAuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping("/audit/logins/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDIT_ADMIN')")
    @Operation(summary = "Get recent user logins", description = "Retrieve recent user login events")
    @ApiResponse(responseCode = "200", description = "Recent logins retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Audit admin role required")
    public ResponseEntity<List<UserEventAuditDTO>> getRecentLogins() {
        List<UserEventAuditDTO> logins = auditService.getRecentLogins();
        return ResponseEntity.ok(logins);
    }

    @GetMapping("/audit/user-events")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDIT_ADMIN')")
    @Operation(summary = "Get paginated user events", description = "Retrieve user events with pagination")
    @ApiResponse(responseCode = "200", description = "User events retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Audit admin role required")
    public ResponseEntity<Page<UserEventAuditDTO>> getUserEventLogPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserEventAuditDTO> events = auditService.getUserEventLogPaginated(page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/audit/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDIT_ADMIN')")
    @Operation(summary = "Get paginated transaction audit", description = "Retrieve transaction audit records with pagination")
    @ApiResponse(responseCode = "200", description = "Transaction audit retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Audit admin role required")
    public ResponseEntity<Page<TransactionAuditDTO>> getTransactionAuditPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionAuditDTO> audits = auditService.getTransactionAuditPaginated(page, size);
        return ResponseEntity.ok(audits);
    }

    @GetMapping("/audit/transactions/{auditId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDIT_ADMIN')")
    @Operation(summary = "Get transaction details for audit", description = "Retrieve detailed transaction information including account owner details for a specific audit record")
    @ApiResponse(responseCode = "200", description = "Transaction details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Audit record not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Audit admin role required")
    public ResponseEntity<List<TransactionWithUserDTO>> getTransactionDetailsForAudit(@PathVariable Long auditId) {
        List<TransactionWithUserDTO> transactions = auditService.getTransactionDetailsForAudit(auditId);
        return ResponseEntity.ok(transactions);
    }
}
