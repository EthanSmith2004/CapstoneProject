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
public class AdminFinanceLoadResponse {
    private Long auditId;
    private Integer totalRequests;
    private Integer successfulLoads;
    private Integer failedLoads;
    private BigDecimal totalAmountLoaded;
    private List<AdminLoadCreditResult> results;
    private Instant processedAt;
    private CompactUserDTO processedBy;
}


