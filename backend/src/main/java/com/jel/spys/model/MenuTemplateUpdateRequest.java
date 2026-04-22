package com.jel.spys.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTemplateUpdateRequest {
    private String description;

    @Min(value = 0, message = "Delivery offset must be non-negative")
    @Max(value = 10080, message = "Delivery offset cannot exceed 10080 minutes (1 week)")
    private Integer deliveryOffsetMinutes;

    private Integer releaseOffsetMinutes;

    private Integer orderByOffsetMinutes;

    @Size(max = 100, message = "Preset name cannot exceed 100 characters")
    private String presetName;
}