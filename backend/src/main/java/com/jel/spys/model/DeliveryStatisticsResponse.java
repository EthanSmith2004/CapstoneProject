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
@Schema(description = "Delivery statistics for the dashboard")
public class DeliveryStatisticsResponse {
    
    @Schema(description = "Total items to deliver today")
    private Integer totalItemsToday;
    
    @Schema(description = "Items currently in delivery status")
    private Integer itemsInDelivery;
    
    @Schema(description = "Items successfully delivered today")
    private Integer itemsDelivered;
    
    @Schema(description = "Completion percentage", example = "75.5")
    private Double completionPercentage;
}
