package com.jel.spys.model;

import java.math.BigDecimal;

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
public class AdminLoadCreditResult {
    private String identifier; // credential number or email
    private Boolean success;
    private BigDecimal amountLoaded;
    private String errorMessage;
    private Long transactionId;
}