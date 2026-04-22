package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPeriodStatistics {
    private Instant periodStart;
    private Instant periodEnd;
    private Double averageRating;
    private Long totalFeedbackCount;
    private Long positiveFeedbackCount; // Rating >= 4
    private Long negativeFeedbackCount; // Rating <= 2
    private Long neutralFeedbackCount;  // Rating = 3
    private Double satisfactionRate; // positiveFeedback / total * 100
}
