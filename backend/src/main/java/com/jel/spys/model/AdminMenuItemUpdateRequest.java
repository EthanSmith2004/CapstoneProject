package com.jel.spys.model;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMenuItemUpdateRequest {
    private String name;
    private String description;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private Long kcal;
    private Instant deliveryDate;
    private Instant editBy; // Edit window deadline
    private String imageHero;
    private String imageDetail;
    private List<Long> allergyIds;
    private Instant releaseDate;
    private Instant orderBy;
}
