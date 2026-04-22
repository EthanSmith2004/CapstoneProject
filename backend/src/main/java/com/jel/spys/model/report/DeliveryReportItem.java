package com.jel.spys.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReportItem {
    private Instant deliveryDate;
    private String itemName;
    private Long quantity;
    private String firstName;
    private String lastName;
    private String credentialNumber;
    private String residenceName;
    private String campusName;
}
