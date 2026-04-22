package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response containing user details and pending delivery items")
public class UserDeliveryItemsResponse {
    
    @Schema(description = "User's credential number/barcode")
    private String credentialNumber;
    
    @Schema(description = "User's first name")
    private String firstName;
    
    @Schema(description = "User's last name")
    private String lastName;
    
    @Schema(description = "User's email")
    private String email;
    
    @Schema(description = "User's residence")
    private String residence;
    
    @Schema(description = "User's campus")
    private String campus;
    
    @Schema(description = "List of order items pending delivery for this user")
    private List<OrderItemDTO> pendingItems;
    
    @Schema(description = "Total number of items pending delivery")
    private Integer totalItems;
}
