package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTemplateDTO {
    private Long id;
    private String description;
    private Integer deliveryOffsetMinutes;
    private Integer releaseOffsetMinutes;
    private Integer orderByOffsetMinutes;
    private String presetName;
    private Instant createdAt;
    private Instant updatedAt;
}