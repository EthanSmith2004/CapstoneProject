package com.jel.spys.model.report;

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
public class KitchenReportData {
    private Instant startDate;
    private Instant endDate;
    private Instant generatedAt;
    private List<KitchenReportItem> items;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
    private Integer uniqueItems;
}
