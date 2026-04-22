package com.jel.spys.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenReportItem {
    private String name;
    private Long quantity;
    private BigDecimal totalSales;
}
