package com.jel.spys.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemQueueRequest {
    @NotNull(message = "Source menu item ID is required")
    private Long sourceMenuItemId;
    
    @NotNull(message = "Delivery date is required")
    private Instant deliveryDate;
    
    @NotNull(message = "Release date is required")
    private Instant releaseDate;
    
    @NotNull(message = "Edit by date is required")
    private Instant editBy;
    
    @NotNull(message = "Order by date is required")
    private Instant orderBy;
}
