package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request to scan a user barcode for delivery")
public class ScanUserBarcodeRequest {
    
    @NotBlank(message = "Barcode is required")
    @Schema(description = "The scanned user barcode/credential number", example = "123456789")
    private String barcode;
}
