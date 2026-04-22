package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderPeriodStatistics {
    private Instant periodStart;
    private Instant periodEnd;

    private List<AdminOrderStatisticLine> statistics;
}
