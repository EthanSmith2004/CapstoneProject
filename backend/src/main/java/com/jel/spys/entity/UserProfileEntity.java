package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profile", indexes = {
        @Index(name = "idx_user_profile_credential", columnList = "credential_number"),
        @Index(name = "idx_user_profile_user", columnList = "user_id"),
        @Index(name = "idx_user_profile_campus", columnList = "campus_id"),
        @Index(name = "idx_user_profile_residence", columnList = "residence_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(columnName = "enabled", strategy = SoftDeleteType.ACTIVE)
@EntityListeners(AuditingEntityListener.class)
public class UserProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true, name = "credential_number")
    private String credentialNumber;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campus_id")
    private CampusEntity campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residence_id")
    private ResidenceEntity residence;

    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<AllergyEntity> allergy = new ArrayList<>();

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;
}
