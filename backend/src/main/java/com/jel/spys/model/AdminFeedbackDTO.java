package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jel.spys.entity.OrderItemFeedbackEntity;
import com.jel.spys.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminFeedbackDTO {
    private Long id;
    private Long sentiment;
    private String category;
    private CompactUserDTO user;
    private Long menuItemId;
    private String menuItemName;
    private Integer rating; // 1-5 stars
    private String comment;
    private Instant createdAt;

    public AdminFeedbackDTO(OrderItemFeedbackEntity entity) {
        this(
                entity.getId(),
                entity.getSentiment(),
                entity.getCategory(),
                null,
                entity.getOrderItem().getId(),
                entity.getOrderItem().getName(),
                entity.getRating(),
                entity.getFeedback(),
                entity.getCreatedAt()
        );
    }

    public AdminFeedbackDTO(OrderItemFeedbackEntity entity, UserEntity user) {
        this(
                entity.getId(),
                entity.getSentiment(),
                entity.getCategory(),
                new CompactUserDTO(user),
                entity.getOrderItem().getId(),
                entity.getOrderItem().getName(),
                entity.getRating(),
                entity.getFeedback(),
                entity.getCreatedAt()
        );
    }
}
