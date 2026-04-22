package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="transaction_audit")
@Getter
@Immutable
@RequiredArgsConstructor
public class TransactionAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private final UserEntity user;

    @Column(name = "initiated_at", nullable = false)
    private final Instant initiatedAt;

    @Column(name = "loaded_content", nullable = false)
    private final String loadedContent;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private final TransactionAuditType type;

    @OneToMany
    @JoinTable(
            name = "audit_transaction_link",
            joinColumns = @JoinColumn(name = "audit_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private final List<TransactionEntity> transactions = new ArrayList<>();

    protected TransactionAuditEntity() {
        user = null;
        initiatedAt = null;
        loadedContent = null;
        type = null;
    }
}
