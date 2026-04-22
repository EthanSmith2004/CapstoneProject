package com.jel.spys.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
public class OrderItemDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Instant deliveryDate;
    private BigDecimal totalPrice;
    private List<String> allergies;
    private MenuItemDTO menuItem;
    private FeedbackDTO feedback;
    private OrderItemStatus status;
    private Instant editBy;
    private Instant dateTimeDelivered;
}
