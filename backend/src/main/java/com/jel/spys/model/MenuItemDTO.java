package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItemDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long kcal;
    private Instant deliveryDate;
    private Instant editBy; 
    private String imageHero;
    private String imageDetail;
    private List<String> allergies;
    private Instant releaseDate;
    private Instant orderBy;
    private boolean isReleased;
    private Instant createdAt;
    private Instant updatedAt;
}
