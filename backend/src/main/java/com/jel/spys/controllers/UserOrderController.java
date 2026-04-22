package com.jel.spys.controllers;

import com.jel.spys.facade.UserOrderFacade;
import com.jel.spys.model.*;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User Orders", description = "User order management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserOrderController {

    @Autowired
    private UserOrderFacade userOrderFacade;

    @GetMapping("/orders/schedule")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user order item schedule", description = "Retrieve all pending order items for current user")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderItemDTO>> getUserOrders() {
        List<OrderItemDTO> orders = userOrderFacade.getUserOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/historic")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user order history", description = "Retrieve historic user orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    public ResponseEntity<List<OrderItemDTO>> getUserOrderHistory() {
        // TODO pagination
        List<OrderItemDTO> orders = userOrderFacade.getUserOrderHistory();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orders") 
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create a new order", description = "Create a new order for the current user")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody PlaceOrderRequest orderCreateDTO) {
        OrderDTO createdOrder = userOrderFacade.createOrder(orderCreateDTO);
        return ResponseEntity.status(201).body(createdOrder);
    }

    @DeleteMapping("/orders/{orderItemId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Cancel an order item", description = "Cancel a specific order item for the current user")
    @ApiResponse(responseCode = "200", description = "Order item cancelled successfully")
    public ResponseEntity<Void> cancelOrderItem(@PathVariable Long orderItemId) {
        userOrderFacade.cancelOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }
}
