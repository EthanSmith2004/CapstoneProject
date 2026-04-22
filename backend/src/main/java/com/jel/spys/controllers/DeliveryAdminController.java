package com.jel.spys.controllers;

import com.jel.spys.model.*;
import com.jel.spys.service.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@Slf4j
@Tag(name = "Delivery Admin", description = "Endpoints for delivery admin barcode scanning and order delivery management")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('DELIVERY_ADMIN') and hasRole('ADMIN')")
public class DeliveryAdminController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/scan-user")
    @Operation(summary = "Scan user barcode", 
               description = "Scan a user's barcode to retrieve their pending delivery items for today")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's pending items",
                content = @Content(schema = @Schema(implementation = UserDeliveryItemsResponse.class)))
    @ApiResponse(responseCode = "404", description = "User not found with provided barcode")
    public ResponseEntity<UserDeliveryItemsResponse> scanUserBarcode(@Valid @RequestBody ScanUserBarcodeRequest request) {
        log.info("Scanning user barcode: {}", request.getBarcode());
        UserDeliveryItemsResponse response = deliveryService.getUserPendingDeliveryItems(request.getBarcode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scan-item")
    @Operation(summary = "Scan item for delivery confirmation",
               description = "Mark an order item as delivered by scanning user barcode and confirming item delivery")
    @ApiResponse(responseCode = "200", description = "Item successfully marked as delivered",
                content = @Content(schema = @Schema(implementation = DeliveryConfirmationResponse.class)))
    @ApiResponse(responseCode = "404", description = "User or item not found")
    @ApiResponse(responseCode = "400", description = "Item does not belong to user or not in correct status")
    public ResponseEntity<DeliveryConfirmationResponse> scanItemForDelivery(@Valid @RequestBody ScanItemBarcodeRequest request) {
        log.info("Marking item {} as delivered for user barcode: {}", request.getOrderItemId(), request.getUserBarcode());
        DeliveryConfirmationResponse response = deliveryService.markItemAsDelivered(
                request.getUserBarcode(), request.getOrderItemId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get delivery statistics",
               description = "Get delivery statistics for today's deliveries")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved delivery statistics",
                content = @Content(schema = @Schema(implementation = DeliveryStatisticsResponse.class)))
    public ResponseEntity<DeliveryStatisticsResponse> getDeliveryStatistics() {
        DeliveryStatisticsResponse statistics = deliveryService.getDeliveryStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/pending-deliveries")
    @Operation(summary = "Get all pending deliveries",
               description = "Get all users with pending deliveries for today, grouped by user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved pending deliveries")
    public ResponseEntity<List<UserDeliveryItemsResponse>> getAllPendingDeliveries() {
        List<UserDeliveryItemsResponse> pendingDeliveries = deliveryService.getAllPendingDeliveries();
        return ResponseEntity.ok(pendingDeliveries);
    }
}
