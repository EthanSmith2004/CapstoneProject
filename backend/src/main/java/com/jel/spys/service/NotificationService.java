package com.jel.spys.service;

import com.jel.spys.entity.NotificationEntity;
import com.jel.spys.entity.Role;
import com.jel.spys.entity.UserDeviceEntity;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.model.NotificationDTO;
import com.jel.spys.model.NotificationRequest;
import com.jel.spys.model.PushSubscriptionRequest;
import com.jel.spys.repository.NotificationRepository;
import com.jel.spys.repository.UserDeviceRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    private final PushService pushService;
    private final UserDeviceRepository userDeviceRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final ClockService clockService;

    public NotificationService(UserDeviceRepository userDeviceRepository,
                               NotificationRepository notificationRepository,
                               @Value("${vapid.public.key}") String publicKey,
                               @Value("${vapid.private.key}") String privateKey,
                               ObjectMapper objectMapper, UserService userService, ClockService clockService) throws GeneralSecurityException {
        this.userDeviceRepository = userDeviceRepository;
        this.notificationRepository = notificationRepository;
        this.pushService = new PushService(publicKey, privateKey);
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.clockService = clockService;
    }

    public void registerDevice(Long userId, PushSubscriptionRequest request) {
        UserDeviceEntity device = userDeviceRepository.findByEndpoint(request.getEndpoint())
                .orElse(new UserDeviceEntity());

        device.setId(userId);
        device.setEndpoint(request.getEndpoint());
        device.setP256dh(request.getP256dh());
        device.setAuth(request.getAuth());
        device.setUserAgent(request.getUserAgent());
        device.setIsActive(true);

        userDeviceRepository.save(device);
    }

    public void registerDevice(UserEntity user, PushSubscriptionRequest request) {
        registerDevice(user.getId(), request);
    }

    public void unregisterDevice(String endpoint) {
        userDeviceRepository.findByEndpoint(endpoint).ifPresent(device -> {
            device.setIsActive(false);
            userDeviceRepository.save(device);
        });
    }

    public void unregisterDevice(UserEntity user, PushSubscriptionRequest request) {
        if (request.getEndpoint() == null || request.getEndpoint().isEmpty()) {
            log.warn("Attempted to unregister device with null or empty endpoint for user {}", user.getId());
            return;
        }
        
        userDeviceRepository.findByEndpoint(request.getEndpoint()).ifPresent(device -> {
            if (device.getId().equals(user.getId())) {
                device.setIsActive(false);
                userDeviceRepository.save(device);
            } else {
                log.error("Attempted to unregister device that does not belong to user {}: {}", user.getId(), request.getEndpoint());
                throw new IllegalArgumentException("Device does not belong to user");
            }
        });

    }

    @Async
    public void sendNotificationToUser(Long userId, NotificationRequest notificationRequest) {
        storeNotification(userId, notificationRequest);
        List<UserDeviceEntity> devices = userDeviceRepository.findByUserId(userId);
        for (UserDeviceEntity device : devices) {
            if (device.getIsActive()) {
                sendPushNotification(device, notificationRequest);
            }
        }
    }

    @Async
    public void sendNotificationToAll(NotificationRequest notificationRequest) {
        storeNotification(notificationRequest);

        List<UserDeviceEntity> devices = userDeviceRepository.findByIsActiveTrue();
        for (UserDeviceEntity device : devices) {
            sendPushNotification(device, notificationRequest);
        }
    }

    public void storeNotification(NotificationRequest notificationRequest) {
        List<UserEntity> users = userService.getUserEntitiesByRole(Role.USER);

        for  (UserEntity user : users) {
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setUser(user);
            notificationEntity.setMessage(notificationRequest.getBody());
            notificationEntity.setTitle(notificationRequest.getTitle());
            notificationEntity.setType(notificationRequest.getType());
            notificationRepository.save(notificationEntity);
        }
    }

    public void storeNotification(Long userId, NotificationRequest notificationRequest) {
        UserEntity user = userService.getUserById(userId);
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUser(user);
        notificationEntity.setMessage(notificationRequest.getBody());
        notificationEntity.setTitle(notificationRequest.getTitle());
        notificationEntity.setType(notificationRequest.getType());
        notificationRepository.save(notificationEntity);
    }

    /**
     * Get all notifications for a user
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(UserEntity user) {
        List<NotificationEntity> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications for a user
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(UserEntity user) {
        List<NotificationEntity> notifications = notificationRepository.findUnreadNotificationsByUser(user);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notification count for a user
     */
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(UserEntity user) {
        return notificationRepository.countUnreadNotificationsByUser(user);
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId, UserEntity user) {
        notificationRepository.markAsRead(notificationId, user, clockService.now());
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllNotificationsAsRead(UserEntity user) {
        notificationRepository.markAllAsReadForUser(user, clockService.now());
    }

    /**
     * Create and save a notification (without sending push notification)
     */
    @Transactional
    public NotificationEntity createNotification(NotificationEntity notification) {
        return notificationRepository.save(notification);
    }

    /**
     * Convert NotificationEntity to NotificationDTO
     */
    private NotificationDTO convertToDTO(NotificationEntity entity) {
        return NotificationDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType())
                .status(entity.getStatus())
                .deepLinkUrl(entity.getDeepLinkUrl())
                .imageUrl(entity.getImageUrl())
                .relatedOrderId(entity.getRelatedOrder() != null ? entity.getRelatedOrder().getId() : null)
                .scheduledFor(entity.getScheduledFor())
                .sentAt(entity.getSentAt())
                .deliveredAt(entity.getDeliveredAt())
                .readAt(entity.getReadAt())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .isExpired(entity.isExpired())
                .isReadyToSend(entity.isReadyToSend())
                .build();
    }

    private void sendPushNotification(UserDeviceEntity device, NotificationRequest notificationRequest) {
        try {
            Subscription subscription = new Subscription(device.getEndpoint(), new Subscription.Keys(device.getP256dh(), device.getAuth()));
            
            NotificationPayload payload = new NotificationPayload(
                notificationRequest.getTitle(),
                notificationRequest.getBody(),
                notificationRequest.getIcon()
            );
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            Notification notification = new Notification(subscription, jsonPayload);
            pushService.send(notification);
            
        } catch (GeneralSecurityException | IOException | ExecutionException | InterruptedException | JoseException e) {
           log.error("Failed to send notification to device {}: {}", device.getEndpoint(), e.getMessage(), e); 
        }
    }
    @Data
    @AllArgsConstructor
    private static class NotificationPayload {
        private final String title;
        private final String body;
        private final String icon;
    }
}
