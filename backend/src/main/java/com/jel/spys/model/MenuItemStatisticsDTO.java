package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemStatisticsDTO {
    private Long menuItemId;
    private String menuItemName;
    private Long totalOrders;
    private Long totalQuantity;
    private java.math.BigDecimal totalRevenue;
}
