package com.jel.spys.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReportData {
    private Instant startDate;
    private Instant endDate;
    private Instant generatedAt;
    private List<DeliveryReportItem> deliveries;
    private Integer totalDeliveries;
    private Integer uniqueCampuses;
    private Integer uniqueResidences;
}
