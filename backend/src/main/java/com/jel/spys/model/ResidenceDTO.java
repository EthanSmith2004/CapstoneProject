package com.jel.spys.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class ResidenceDTO {
    private Long id;
    private String residence;
    private Long userCount;
    private Instant createdAt;
    private Instant updatedAt;
}