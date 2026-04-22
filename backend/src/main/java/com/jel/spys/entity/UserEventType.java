package com.jel.spys.entity;

public enum UserEventType {
    // Authentication events
    LOGIN,
    LOGOUT_IMPLICIT,
    LOGOUT_EXPLICIT,
    TOKEN_REFRESH,
    REGISTER,
    
    // User profile events
    PROFILE_CREATED,
    PROFILE_UPDATED,
    DETAILS_UPDATE,
    
    // User settings events
    SETTINGS_UPDATED,
    
    // Order events
    ORDER_CREATED,
    ORDER_CANCELLED,
    
    // Feedback events
    FEEDBACK_SUBMITTED,
    
    // Admin actions on users
    ADMIN_USER_CREATED,
    ADMIN_USER_UPDATED,
    ADMIN_USER_DELETED,
    ADMIN_DETAILS_UPDATE,
    
    // Admin order management
    ADMIN_ORDER_STATUS_UPDATED,
    ADMIN_ORDER_ITEM_STATUS_UPDATED,
    ADMIN_BULK_ORDER_STATUS_UPDATED,
    
    // Admin menu management
    ADMIN_MENU_CREATED,
    ADMIN_MENU_UPDATED,
    ADMIN_MENU_DELETED,
    ADMIN_MENU_TEMPLATE_CREATED,
    ADMIN_MENU_TEMPLATE_UPDATED,
    ADMIN_MENU_TEMPLATE_DELETED,
    
    // Admin notifications
    ADMIN_NOTIFICATION_SENT_TO_ALL,
    ADMIN_NOTIFICATION_SENT_TO_USER
}
