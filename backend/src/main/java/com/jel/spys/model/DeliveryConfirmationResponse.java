package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response after marking an item as delivered")
public class DeliveryConfirmationResponse {
    
    @Schema(description = "The delivered order item")
    private OrderItemDTO deliveredItem;
    
    @Schema(description = "Success message")
    private String message;
    
    @Schema(description = "Number of remaining items for this user")
    private Integer remainingItems;
}
