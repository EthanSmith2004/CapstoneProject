package com.jel.spys.entity;

import com.jel.spys.service.ClockService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu_item")
@EntityListeners(AuditingEntityListener.class)
public class MenuItemEntity extends AbstractItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    @Column(name = "release_date", nullable = true)
    private Instant releaseDate;

    @Column(name = "order_by", nullable = true)
    private Instant orderBy;

    public MenuItemEntity(String name, String description, double price, Long kcal, String image) {
        this.setName(name);
        this.setDescription(description);
        this.setPrice(BigDecimal.valueOf(price));
        this.setKcal(kcal);
        this.setImageHero(image);
        this.setImageDetail(image);
    }

    public boolean isReleased(ClockService clockService) {
        return releaseDate != null && releaseDate.isBefore(clockService.now());
    }

    public boolean isActive(ClockService clockService) {
        return orderBy != null && orderBy.isAfter(clockService.now());
    }

    public boolean isValidOrderItem(ClockService clockService) {
        return orderBy != null &&
                releaseDate != null &&
                this.getDeliveryDate() != null &&
                orderBy.isAfter(clockService.now());
    }
}
