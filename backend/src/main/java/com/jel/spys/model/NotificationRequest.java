package com.jel.spys.model;

import com.jel.spys.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String body;
    private String icon;
    private String url;
    private NotificationType type;
}
