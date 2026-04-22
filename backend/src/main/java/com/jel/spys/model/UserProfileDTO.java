package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {
    private Long id;
    private String credentialNumber; // Student/Staff number
    private CompactUserDTO user;
    private SelectDTO campus;
    private SelectDTO residence;
    private List<SelectDTO> allergies;
    private Instant createdAt;
    private Instant updatedAt;
    private BigDecimal balance;
}
