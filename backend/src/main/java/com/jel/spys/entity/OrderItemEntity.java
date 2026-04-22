package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class OrderItemEntity extends AbstractItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_transaction_id", nullable = true)
    private TransactionEntity refundTransaction;

    @OneToOne(mappedBy = "orderItem")
    private OrderItemFeedbackEntity feedback;

    @Column(name = "date_time_delivered", nullable = true)
    private Instant dateTimeDelivered;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        if (status == OrderItemStatus.DELIVERED && dateTimeDelivered == null) {
            dateTimeDelivered = Instant.now();
        }
    }
}
