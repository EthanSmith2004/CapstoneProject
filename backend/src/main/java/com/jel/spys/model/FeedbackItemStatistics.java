package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackItemStatistics {
    private String menuItemName;
    private Long feedbackCount;
    private Double averageRating;
    private Integer minRating;
    private Integer maxRating;
    private Map<Integer, Long> ratingDistribution; // Map of rating -> count
}
