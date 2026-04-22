package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request to scan an item barcode for delivery confirmation")
public class ScanItemBarcodeRequest {
    
    @NotBlank(message = "User barcode is required")
    @Schema(description = "The user's barcode/credential number", example = "123456789")
    private String userBarcode;
    
    @NotNull(message = "Order item ID is required")
    @Schema(description = "The order item ID to mark as delivered", example = "42")
    private Long orderItemId;
}
