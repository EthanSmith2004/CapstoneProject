package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class AbstractItem {
    @Column(name = "name")
    private String name;

    @Column(name = "delivery_date", nullable = true)
    private Instant deliveryDate;

    @Column(name = "description")
    private String description;

    @Column(name = "kcal", nullable = true)
    private Long kcal;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "edit_by", nullable = true)
    private Instant editBy;

    @Column(name = "image_hero")
    private String imageHero;

    @Column(name = "image_detail")
    private String imageDetail;

    @ManyToMany
    @JoinTable(
        joinColumns = @JoinColumn(name = "item_id"),
        inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    private List<AllergyEntity> allergies = new ArrayList<>();
}
