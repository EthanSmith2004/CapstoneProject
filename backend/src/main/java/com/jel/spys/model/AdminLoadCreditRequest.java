package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminLoadCreditRequest {
    private String credentialNumber; // Student/Staff number
    private String email; // Alternative identifier
    private BigDecimal amount;
    private String description;
}
