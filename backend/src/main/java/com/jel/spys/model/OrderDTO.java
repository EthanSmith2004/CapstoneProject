package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jel.spys.entity.OrderStatus;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    private Long id;
    private CompactUserDTO user;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
    private TransactionDTO transaction;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean canEdit;
    private boolean canCancel;
    private boolean canPay;
}

