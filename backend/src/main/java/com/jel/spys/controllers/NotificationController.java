package com.jel.spys.controllers;

import com.jel.spys.facade.UserNotificationFacade;
import com.jel.spys.model.NotificationDTO;
import com.jel.spys.model.PushSubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Notifications", description = "Push notification endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    @Autowired
    private UserNotificationFacade notificationFacade;

    @Value("${vapid.public.key}")
    private String vaPublicKey;

    @PostMapping("/notifications/subscribe")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Subscribe to push notifications", description = "Register a device for push notifications.")
    @ApiResponse(responseCode = "201", description = "Subscribed successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody PushSubscriptionRequest subscription) {
        notificationFacade.registerDevice(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/notifications/publicKey")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get public key", description = "Get the backend public key")
    @ApiResponse(responseCode = "200", description = "Got public key successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> publicKey() {
        return ResponseEntity.ok(vaPublicKey);
    }

    @PostMapping("/notifications/unsubscribe")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Unsubscribe from push notifications", description = "Unregister a device for push notifications.")
    @ApiResponse(responseCode = "200", description = "Unsubscribed successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> unsubscribe(@RequestBody PushSubscriptionRequest subscription) {
        notificationFacade.unregisterDevice(subscription);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all notifications", description = "Get all notifications for the current user")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(notificationFacade.getUserNotifications());
    }

    @GetMapping("/notifications/unread")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get unread notifications", description = "Get all unread notifications for the current user")
    @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationFacade.getUnreadNotifications());
    }

    @GetMapping("/notifications/unread/count")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get unread notification count", description = "Get count of unread notifications for the current user")
    @ApiResponse(responseCode = "200", description = "Unread notification count retrieved successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        return ResponseEntity.ok(notificationFacade.getUnreadNotificationCount());
    }

    @PatchMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    @ApiResponse(responseCode = "200", description = "Notification marked as read successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationFacade.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/notifications/read-all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the current user")
    @ApiResponse(responseCode = "200", description = "All notifications marked as read successfully")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        notificationFacade.markAllNotificationsAsRead();
        return ResponseEntity.ok().build();
    }

}
