package com.jel.spys.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTemplateCreateRequest {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Delivery offset is required")
    @Min(value = 0, message = "Delivery offset must be non-negative")
    @Max(value = 10080, message = "Delivery offset cannot exceed 10080 minutes (1 week)")
    private Integer deliveryOffsetMinutes;

    @NotNull(message = "Release offset is required")
    private Integer releaseOffsetMinutes;

    @NotNull(message = "Order by offset is required")
    private Integer orderByOffsetMinutes;

    @NotBlank(message = "Preset name is required")
    @Size(max = 100, message = "Preset name cannot exceed 100 characters")
    private String presetName;
}