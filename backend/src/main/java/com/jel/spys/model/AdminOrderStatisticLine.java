package com.jel.spys.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderStatisticLine {
    private String itemName;
    private String itemStatus;
    private Instant deliveryDate;
    private Integer itemCount;
    private BigDecimal totalRevenue;
}
