package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jel.spys.entity.NotificationStatus;
import com.jel.spys.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type; 
    private NotificationStatus status; 
    private String deepLinkUrl;
    private String imageUrl;
    private Long relatedOrderId;
    private Instant scheduledFor;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean isExpired;
    private boolean isReadyToSend;
}
