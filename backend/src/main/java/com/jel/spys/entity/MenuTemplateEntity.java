package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu_template")
@EntityListeners(AuditingEntityListener.class)
public class MenuTemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "delivery_offset_minutes", nullable = false)
    private Integer deliveryOffsetMinutes; // Minutes from Monday 00:00 UTC

    @Column(name = "release_offset_minutes", nullable = false)
    private Integer releaseOffsetMinutes; // Minutes before delivery

    @Column(name = "order_by_offset_minutes", nullable = false)
    private Integer orderByOffsetMinutes; // Minutes before delivery

    @Column(name = "preset_name", nullable = false)
    private String presetName;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;
}