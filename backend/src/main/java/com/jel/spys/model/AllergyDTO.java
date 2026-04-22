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
class AllergyDTO {
    private Long id;
    private String allergy;
    private Long userCount;
    private Instant createdAt;
    private Instant updatedAt;
}