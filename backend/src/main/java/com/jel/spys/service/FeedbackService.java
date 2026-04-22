package com.jel.spys.service;

import com.jel.spys.entity.OrderItemEntity;
import com.jel.spys.entity.OrderItemFeedbackEntity;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.exception.RequestStateValidationException;
import com.jel.spys.exception.ResourceNotFoundException;
import com.jel.spys.model.AdminFeedbackDTO;
import com.jel.spys.model.FeedbackDTO;
import com.jel.spys.model.FeedbackItemStatistics;
import com.jel.spys.model.FeedbackPeriodStatistics;
import com.jel.spys.model.FeedbackRequest;
import com.jel.spys.model.FeedbackSentimentRequest;
import com.jel.spys.repository.OrderItemFeedbackRepository;
import com.jel.spys.repository.OrderItemRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    @Autowired
    private OrderItemFeedbackRepository orderItemFeedbackRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public FeedbackDTO placeUserFeedback(UserEntity user, @Valid FeedbackRequest feedback) {
        var builder = OrderItemFeedbackEntity.builder();

        OrderItemEntity orderItem = orderItemRepository.findByUserIdAndId(user.getId(), feedback.getMenuItemId()).orElseThrow(
                () -> new ResourceNotFoundException("Order item does not exist or does not belong to user")
        );

        if (orderItemFeedbackRepository.findByOrderItem(orderItem).isPresent()) {
            throw new RequestStateValidationException("Order Item feedback already exists");
        }

        builder.feedback(feedback.getComment());
        builder.rating(feedback.getRating());
        builder.orderItem(orderItem);
        OrderItemFeedbackEntity entity = builder.build();

        entity = orderItemFeedbackRepository.save(entity);

        return new FeedbackDTO(entity, user);
    }

    public List<AdminFeedbackDTO> getAllFeedback() {
        List<OrderItemFeedbackEntity> feedback = orderItemFeedbackRepository.findAll();

        // TODO FIX SLOW!!!!
        return feedback.stream().map(fb -> new AdminFeedbackDTO(fb, fb.getOrderItem().getOrder().getUser())).collect(Collectors.toList());
    }

    public void setFeedbackSentiment(List<FeedbackSentimentRequest> feedback) {
        for (FeedbackSentimentRequest request : feedback) {
            OrderItemFeedbackEntity entity = orderItemFeedbackRepository.findById(request.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Feedback with id " + request.getId() + " not found")
            );
            entity.setSentiment(request.getSentiment());
            entity.setCategory(request.getCategory());
            orderItemFeedbackRepository.save(entity);
        }
    }

    public FeedbackPeriodStatistics getFeedbackStatisticsPeriod(Instant start, Instant end, Long menuItemId) {
        Object[] rawStats = orderItemFeedbackRepository.getFeedbackStatistics(start, end, menuItemId).get(0);

        // Extract values with null safety
        Double avgRating = rawStats[0] != null ? ((Number) rawStats[0]).doubleValue() : 0.0;
        long totalCount = rawStats[1] != null ? ((Number) rawStats[1]).longValue() : 0L;
        long positiveCount = rawStats[2] != null ? ((Number) rawStats[2]).longValue() : 0L;
        Long neutralCount = rawStats[3] != null ? ((Number) rawStats[3]).longValue() : 0L;
        Long negativeCount = rawStats[4] != null ? ((Number) rawStats[4]).longValue() : 0L;
        
        // If total count is 0, return empty statistics
        if (totalCount == 0) {
            return FeedbackPeriodStatistics.builder()
                    .periodStart(start)
                    .periodEnd(end)
                    .averageRating(0.0)
                    .totalFeedbackCount(0L)
                    .positiveFeedbackCount(0L)
                    .neutralFeedbackCount(0L)
                    .negativeFeedbackCount(0L)
                    .satisfactionRate(0.0)
                    .build();
        }
        
        Double satisfactionRate = (positiveCount * 100.0 / totalCount);
        
        return FeedbackPeriodStatistics.builder()
                .periodStart(start)
                .periodEnd(end)
                .averageRating(avgRating)
                .totalFeedbackCount(totalCount)
                .positiveFeedbackCount(positiveCount)
                .neutralFeedbackCount(neutralCount)
                .negativeFeedbackCount(negativeCount)
                .satisfactionRate(satisfactionRate)
                .build();
    }

    public List<FeedbackItemStatistics> getFeedbackStatisticsByItem(Instant start, Instant end) {
        List<Object[]> rawData = orderItemFeedbackRepository.getFeedbackByMenuItem(start, end);
        
        if (rawData == null || rawData.isEmpty()) {
            return List.of(); // Return empty list if no data
        }
        
        return rawData.stream().map(row -> {
            // Validate row has enough columns
            if (row == null || row.length < 5) {
                return null;
            }
            
            String itemName = (String) row[0];
            Long feedbackCount = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Double avgRating = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Integer minRating = row[3] != null ? (Integer) row[3] : 0;
            Integer maxRating = row[4] != null ? (Integer) row[4] : 0;
            
            // Get rating distribution for this item
            List<Object[]> distribution = orderItemFeedbackRepository.getRatingDistributionForItemInPeriod(itemName, start, end);
            Map<Integer, Long> ratingMap = new HashMap<>();
            
            if (distribution != null && !distribution.isEmpty()) {
                for (Object[] dist : distribution) {
                    if (dist != null && dist.length >= 2 && dist[0] != null && dist[1] != null) {
                        Integer rating = (Integer) dist[0];
                        Long count = ((Number) dist[1]).longValue();
                        ratingMap.put(rating, count);
                    }
                }
            }
            
            // Fill in missing ratings with 0
            for (int i = 1; i <= 5; i++) {
                ratingMap.putIfAbsent(i, 0L);
            }
            
            return FeedbackItemStatistics.builder()
                    .menuItemName(itemName)
                    .feedbackCount(feedbackCount)
                    .averageRating(avgRating)
                    .minRating(minRating)
                    .maxRating(maxRating)
                    .ratingDistribution(ratingMap)
                    .build();
        })
        .filter(stat -> stat != null) // Filter out any null results
        .collect(Collectors.toList());
    }

    public List<AdminFeedbackDTO> getFeedbackFiltered(Instant start, Instant end, Integer minRating, Integer maxRating, String menuItemName) {
        List<OrderItemFeedbackEntity> feedback = orderItemFeedbackRepository.findFeedbackFiltered(start, end, minRating, maxRating, menuItemName);
        
        return feedback.stream()
                .map(fb -> new AdminFeedbackDTO(fb, fb.getOrderItem().getOrder().getUser()))
                .collect(Collectors.toList());
    }
}
