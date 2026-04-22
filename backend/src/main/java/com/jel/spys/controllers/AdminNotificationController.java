package com.jel.spys.controllers;

import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.NotificationRequest;
import com.jel.spys.service.NotificationService;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Notifications", description = "Admin push notification endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @PostMapping("/notifications/send-to-all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send notification to all users", description = "Send a push notification to all subscribed devices.")
    @ApiResponse(responseCode = "200", description = "Notification sent")
    public ResponseEntity<Void> sendToAll(@RequestBody NotificationRequest notification) {
        notificationService.sendNotificationToAll(notification);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_NOTIFICATION_SENT_TO_ALL);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications/send-to-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send notification to a specific user", description = "Send a push notification to a specific user's devices.")
    @ApiResponse(responseCode = "200", description = "Notification sent")
    public ResponseEntity<Void> sendToUser(@PathVariable Long userId, @RequestBody NotificationRequest notification) {
        notificationService.sendNotificationToUser(userId, notification);
        
        // Log admin action
        userEventService.logEvent(userService.getCurrentUser(), UserEventType.ADMIN_NOTIFICATION_SENT_TO_USER);
        
        return ResponseEntity.ok().build();
    }
}
