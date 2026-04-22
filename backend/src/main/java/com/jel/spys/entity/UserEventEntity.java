package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "user_event", indexes = {
    @Index(name = "idx_user_event_user", columnList = "user_id"),
    @Index(name = "idx_user_event_type", columnList = "event_type"),
    @Index(name = "idx_user_event_time", columnList = "login_event"),
    @Index(name = "idx_user_event_user_type", columnList = "user_id, event_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@EntityListeners(AuditingEntityListener.class)
public class UserEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "login_event")
    @CreatedDate
    private Instant time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, name = "event_type")
    @Enumerated(EnumType.STRING)
    private UserEventType eventType;

}
