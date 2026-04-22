package com.jel.spys.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jel.spys.entity.OrderItemStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkOrderStatusUpdateRequest {
    private Instant deliveryDate;
    private OrderItemStatus status;
    private OrderItemStatus previousStatus;
    private String itemName;
}
