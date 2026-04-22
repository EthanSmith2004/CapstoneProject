package com.jel.spys.facade;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.model.NotificationDTO;
import com.jel.spys.model.PushSubscriptionRequest;
import com.jel.spys.service.NotificationService;
import com.jel.spys.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserNotificationFacade {

    @Autowired
    private NotificationService userNotificationService;

    @Autowired
    private UserService userService;

    /**
     * Get all notifications for the current user
     */
    public List<NotificationDTO> getUserNotifications() {
        UserEntity user = userService.getCurrentUser();
        return userNotificationService.getUserNotifications(user);
    }

    /**
     * Get unread notifications for the current user
     */
    public List<NotificationDTO> getUnreadNotifications() {
        UserEntity user = userService.getCurrentUser();
        return userNotificationService.getUnreadNotifications(user);
    }

    /**
     * Get unread notification count for the current user
     */
    public Long getUnreadNotificationCount() {
        UserEntity user = userService.getCurrentUser();
        return userNotificationService.getUnreadNotificationCount(user);
    }

    /**
     * Mark a notification as read for the current user
     */
    public void markNotificationAsRead(Long notificationId) {
        UserEntity user = userService.getCurrentUser();
        userNotificationService.markNotificationAsRead(notificationId, user);
    }

    /**
     * Mark all notifications as read for the current user
     */
    public void markAllNotificationsAsRead() {
        UserEntity user = userService.getCurrentUser();
        userNotificationService.markAllNotificationsAsRead(user);
    }

    /**
     * Unregister a device from push notifications
     */
    public void unregisterDevice(PushSubscriptionRequest subscription) {
        UserEntity user = userService.getCurrentUser();
        userNotificationService.unregisterDevice(user, subscription);
    }

    /**
     * Register a device for push notifications
     */
    public void registerDevice(@Valid PushSubscriptionRequest subscription) {
        UserEntity user = userService.getCurrentUser();
        userNotificationService.registerDevice(user, subscription);
    }
}
