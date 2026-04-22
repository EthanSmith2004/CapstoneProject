package com.jel.spys.controllers;

import com.jel.spys.entity.OrderStatus;
import com.jel.spys.entity.OrderItemStatus;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.*;
import com.jel.spys.model.report.DeliveryReportData;
import com.jel.spys.model.report.KitchenReportData;
import com.jel.spys.service.OrderService;
import com.jel.spys.service.ReportService;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Order Management", description = "Endpoints for admins to manage user orders")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    @Autowired
    private OrderService adminManagementService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @GetMapping("/orders")
    @Operation(summary = "Get all orders", description = "Retrieve a list of all user orders.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(adminManagementService.getAllOrders());
    }

    @GetMapping("/orders/items")
    @Operation(summary = "Get all order items", description = "Retrieve a list of all order items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of order items")
    public ResponseEntity<List<OrderItemDTO>> getAllOrdersItems() {
        return ResponseEntity.ok(adminManagementService.getAllOrderItems());
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of a specific order.")
    @ApiResponse(responseCode = "200", description = "Successfully updated order status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        OrderDTO result = adminManagementService.updateOrderStatus(id, status);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_ORDER_STATUS_UPDATED);
        
        return ResponseEntity.ok(result);
    }

    @PutMapping("/order-items/{id}/status")
    @Operation(summary = "Update order item status", description = "Update the status of a specific order item with user notification.")
    @ApiResponse(responseCode = "200", description = "Successfully updated order item status")
    public ResponseEntity<OrderItemDTO> updateOrderItemStatus(@PathVariable Long id, @RequestBody OrderItemStatus status) {
        OrderItemDTO result = adminManagementService.updateOrderItemStatus(id, status);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_ORDER_ITEM_STATUS_UPDATED);
        
        return ResponseEntity.ok(result);
    }

    @PutMapping("/orders/bulk-status")
    @Operation(summary = "Bulk update order statuses", description = "Update the statuses of multiple orders at once.")
    @ApiResponse(responseCode = "200", description = "Successfully updated order statuses")
    public ResponseEntity<Void> bulkUpdateOrderStatuses(@RequestBody BulkOrderStatusUpdateRequest request) {
        adminManagementService.bulkUpdateOrderStatuses(request);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_BULK_ORDER_STATUS_UPDATED);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/statistics")
    @Operation(summary = "Get Order statistics", description = "Get order statistics for the default period")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved order statistics")
    public ResponseEntity<AdminOrderStatistics> getOrderStatistics() {
        return ResponseEntity.ok(adminManagementService.getOrderStats());
    }

    @GetMapping("/orders/statistics/detail")
    @Operation(summary = "Get Order statistics", description = "Get order statistics for the specified period with optional campus and residence filters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved order statistics")
    public ResponseEntity<AdminOrderPeriodStatistics> getOrderStatisticsPeriod(
            @RequestParam("start") Instant start, 
            @RequestParam("end") Instant end,
            @RequestParam(value = "campusId", required = false) Long campusId,
            @RequestParam(value = "residenceId", required = false) Long residenceId) {
        return ResponseEntity.ok(adminManagementService.getOrderStatsPeriod(start, end, campusId, residenceId));
    }

    @GetMapping("/orders/report/kitchen")
    @Operation(summary = "Get kitchen report data", description = "Get kitchen report data for orders within a specified date")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved kitchen report data")
    public ResponseEntity<KitchenReportData> getKitchenReportData(@RequestParam("date") Instant date) {
        KitchenReportData reportData = reportService.getKitchenReport(date);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/orders/report/kitchen/period")
    @Operation(summary = "Get kitchen report data for period", description = "Get kitchen report data for orders within a specified period with optional campus and residence filters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved kitchen report data for period")
    public ResponseEntity<KitchenReportData> getKitchenReportPeriod(
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "campusId", required = false) Long campusId,
            @RequestParam(value = "residenceId", required = false) Long residenceId) {
        KitchenReportData reportData = reportService.getKitchenReportPeriod(start, end, campusId, residenceId);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/orders/report/delivery")
    @Operation(summary = "Get delivery report data", description = "Get delivery report data for orders within a specified date")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved delivery report data")
    public ResponseEntity<DeliveryReportData> getDeliveryReportData(@RequestParam("date") Instant date) {
        DeliveryReportData reportData = reportService.getDeliveryReport(date);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/orders/report/delivery/period")
    @Operation(summary = "Get delivery report data for period", description = "Get delivery report data for orders within a specified period with optional campus and residence filters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved delivery report data for period")
    public ResponseEntity<DeliveryReportData> getDeliveryReportPeriod(
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "campusId", required = false) Long campusId,
            @RequestParam(value = "residenceId", required = false) Long residenceId) {
        DeliveryReportData reportData = reportService.getDeliveryReportPeriod(start, end, campusId, residenceId);
        return ResponseEntity.ok(reportData);
    }
}
