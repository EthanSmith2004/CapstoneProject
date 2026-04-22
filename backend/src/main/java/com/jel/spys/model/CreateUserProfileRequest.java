package com.jel.spys.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserProfileRequest {
    @NotBlank(message = "Student/Staff number is required")
    private String credentialNumber;
    
    private Long campusId;
    private Long residenceId;
    private List<Long> allergyIds;
}
