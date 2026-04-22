package com.jel.spys.model;

import java.math.BigDecimal;
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
public class FinancialPeriodStatistic {
    private Instant periodStart;
    private Instant periodEnd;
    private BigDecimal totalRevenue;
    private BigDecimal totalRevenuePending;
    private BigDecimal totalRefunds;
    private BigDecimal totalCreditTransactions;
}
