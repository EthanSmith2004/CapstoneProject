package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderStatistics {
    private Map<String, AdminOrderPeriodStatistics> statistics;
}
