package com.jel.spys.controllers;

import com.jel.spys.model.*;
import com.jel.spys.service.FinanceService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Finance", description = "Admin finance management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminFinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/finance/overview")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Get and overview", description = "Retrieve financial overview data")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<FinancialOverview> overview() {
        FinancialOverview overview = financeService.getFinancialOverview();
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/finance/statistics")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Get and overview", description = "Retrieve financial overview data")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<FinancialPeriodStatistic> getStatistics(@RequestParam("start") Instant start, @RequestParam("end") Instant end) {
        FinancialPeriodStatistic overview = financeService.getPeriodStatistic(start, end);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/finance/transactions")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Search transactions", description = "Search transactions")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Financial admin role required")
    public ResponseEntity<List<AdminTransactionDTO>> findTransactions() {
        List<AdminTransactionDTO> transactions = financeService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/finance/users")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Search users with pagination", description = "Search users with financial information using pagination")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Financial admin role required")
    public ResponseEntity<List<FinanceUserDTO>> findUsers() {
        List<FinanceUserDTO> users = financeService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/finance/users/search")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Search users", description = "Search users with financial information using pagination")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Financial admin role required")
    public ResponseEntity<List<CompactUserDTO>> findUsersSearch(String query, int page, int pageSize) {
        List<CompactUserDTO> users = financeService.searchUsersPaginated(query, page, pageSize);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/finance/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Get user financial details", description = "Retrieve detailed financial information for a specific user")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Financial admin role required")
    public ResponseEntity<FinanceUserDTO> getUserDetail(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        FinanceUserDTO userDetail = financeService.getUserDetail(userId);
        return ResponseEntity.ok(userDetail);
    }

    @PostMapping("/finance/load")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Load user credit", description = "Load user Credit")
    @ApiResponse(responseCode = "200", description = "Load ok")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Financial admin role required")
    public ResponseEntity<AdminLoadCreditResult> loadUserCredit(@RequestBody AdminLoadCreditRequest request) {
        AdminLoadCreditResult result = financeService.loadCredit(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/finance/bulk-load", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FINANCIAL_ADMIN')")
    @Operation(summary = "Bulk load credit", description = "Load credit to multiple user accounts in bulk")
    @ApiResponse(responseCode = "200", description = "Bulk load completed")
    @ApiResponse(responseCode = "400", description = "Invalid bulk load request")
    @ApiResponse(responseCode = "403", description = "Access denied - Financial admin role required")
    public ResponseEntity<AdminFinanceLoadResponse> bulkLoadCredit(@RequestParam MultipartFile csvFile) {
        AdminFinanceLoadResponse response = financeService.bulkLoadCredit(new AdminBulkLoadRequest(csvFile));
        return ResponseEntity.ok(response);
    }
}
