package com.jel.spys.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AdminMenuItemCreateRequest {
    @NotBlank(message = "Menu item name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
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
