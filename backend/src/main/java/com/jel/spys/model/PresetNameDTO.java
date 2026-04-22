package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresetNameDTO {
    private String presetName;
    private Integer templateCount;
    private Instant lastUpdated;
}