package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "notification_preference", indexes = {
    @Index(name = "idx_notif_pref_user", columnList = "user_id"),
    @Index(name = "idx_notif_pref_push_enabled", columnList = "push_enabled"),
    @Index(name = "idx_notif_pref_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationPreferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private Boolean pushEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    @Column(name = "order_updates", nullable = false)
    @Builder.Default
    private Boolean orderUpdates = true;

    @Column(name = "menu_updates", nullable = false)
    @Builder.Default
    private Boolean menuUpdates = true;

    @Column(name = "account_updates", nullable = false)
    @Builder.Default
    private Boolean accountUpdates = true;

    @Column(name = "promotional", nullable = false)
    @Builder.Default
    private Boolean promotional = false;

    @Column(name = "system_announcements", nullable = false)
    @Builder.Default
    private Boolean systemAnnouncements = true;

    @Column(name = "quiet_hours_start")
    private String quietHoursStart; // e.g., "22:00"

    @Column(name = "quiet_hours_end")
    private String quietHoursEnd; // e.g., "08:00"

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

}
