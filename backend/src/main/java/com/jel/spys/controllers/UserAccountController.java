package com.jel.spys.controllers;

import com.jel.spys.facade.UserFinanceFacade;
import com.jel.spys.model.*;
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
@Tag(name = "User Account", description = "User account and transaction endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserAccountController {

    @Autowired
    private UserFinanceFacade financeFacade;

    @GetMapping("/account")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get account details", description = "Retrieve current user's account information")
    @ApiResponse(responseCode = "200", description = "Account retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User or account not found")
    public ResponseEntity<AccountDTO> getAccount() {
        AccountDTO account = financeFacade.getCurrentUserAccount();
        return ResponseEntity.ok(account);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get paginated transactions", description = "Retrieve transactions with pagination")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User or account not found")
    public ResponseEntity<List<TransactionDTO>> getTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TransactionDTO> transactions = financeFacade.getTransactionsPaginated(page, size);
        return ResponseEntity.ok(transactions);
    }
}
