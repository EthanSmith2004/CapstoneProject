package com.jel.spys.repository;

import com.jel.spys.entity.OrderItemEntity;
import com.jel.spys.entity.OrderItemFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemFeedbackRepository extends JpaRepository<OrderItemFeedbackEntity, Long> {
    
    Optional<OrderItemFeedbackEntity> findByOrderItem(OrderItemEntity orderItem);
    
    List<OrderItemFeedbackEntity> findByRating(Integer rating);
    
    List<OrderItemFeedbackEntity> findByRatingGreaterThanEqual(Integer rating);
    
    List<OrderItemFeedbackEntity> findByRatingLessThanEqual(Integer rating);
    
    @Query("SELECT f FROM OrderItemFeedbackEntity f WHERE f.orderItem.name LIKE %:itemName%")
    List<OrderItemFeedbackEntity> findByItemNameContaining(@Param("itemName") String itemName);
    
    @Query("SELECT f FROM OrderItemFeedbackEntity f WHERE f.orderItem.name = :itemName")
    List<OrderItemFeedbackEntity> findByItemName(@Param("itemName") String itemName);
    
    @Query("SELECT f FROM OrderItemFeedbackEntity f WHERE f.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItemFeedbackEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT f FROM OrderItemFeedbackEntity f WHERE f.orderItem.order.user.id = :userId ORDER BY f.createdAt DESC")
    List<OrderItemFeedbackEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT f FROM OrderItemFeedbackEntity f WHERE f.feedback IS NOT NULL AND f.feedback != '' ORDER BY f.createdAt DESC")
    List<OrderItemFeedbackEntity> findWithNonEmptyFeedback();
    
    @Query("SELECT AVG(f.rating) FROM OrderItemFeedbackEntity f WHERE f.orderItem.name = :itemName")
    Double getAverageRatingForItem(@Param("itemName") String itemName);
    
    @Query("SELECT COUNT(f) FROM OrderItemFeedbackEntity f WHERE f.orderItem.name = :itemName")
    Long countFeedbackForItem(@Param("itemName") String itemName);
    
    @Query("SELECT f.rating, COUNT(f) FROM OrderItemFeedbackEntity f WHERE f.orderItem.name = :itemName GROUP BY f.rating ORDER BY f.rating")
    List<Object[]> getRatingDistributionForItem(@Param("itemName") String itemName);
    
    @Query("SELECT AVG(f.rating) FROM OrderItemFeedbackEntity f WHERE f.createdAt BETWEEN :startDate AND :endDate")
    Double getAverageRatingInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT f.orderItem.name, AVG(f.rating) as avgRating FROM OrderItemFeedbackEntity f GROUP BY f.orderItem.name HAVING COUNT(f) >= :minFeedbackCount ORDER BY avgRating DESC")
    List<Object[]> getTopRatedItems(@Param("minFeedbackCount") Long minFeedbackCount);
    
    @Query("SELECT f.orderItem.name, AVG(f.rating) as avgRating FROM OrderItemFeedbackEntity f GROUP BY f.orderItem.name HAVING COUNT(f) >= :minFeedbackCount ORDER BY avgRating ASC")
    List<Object[]> getLowestRatedItems(@Param("minFeedbackCount") Long minFeedbackCount);
    
    // Feedback statistics for a period
    @Query("SELECT " +
           "CAST(AVG(f.rating) AS double) as avgRating, " +
           "COUNT(f) as totalCount, " +
           "SUM(CASE WHEN f.rating >= 4 THEN 1 ELSE 0 END) as positiveCount, " +
           "SUM(CASE WHEN f.rating = 3 THEN 1 ELSE 0 END) as neutralCount, " +
           "SUM(CASE WHEN f.rating <= 2 THEN 1 ELSE 0 END) as negativeCount " +
           "FROM OrderItemFeedbackEntity f " +
           "WHERE f.createdAt BETWEEN :start AND :end " +
           "AND (:menuItemId IS NULL OR f.orderItem.id = :menuItemId)")
    List<Object[]> getFeedbackStatistics(@Param("start") Instant start,
                                    @Param("end") Instant end, 
                                    @Param("menuItemId") Long menuItemId);
    
    // Feedback stats grouped by menu item
    @Query("SELECT f.orderItem.name as itemName, " +
           "COUNT(f) as feedbackCount, " +
           "CAST(AVG(f.rating) AS double) as avgRating, " +
           "MIN(f.rating) as minRating, " +
           "MAX(f.rating) as maxRating " +
           "FROM OrderItemFeedbackEntity f " +
           "WHERE f.createdAt BETWEEN :start AND :end " +
           "GROUP BY f.orderItem.name " +
           "HAVING COUNT(f) > 0 " +
           "ORDER BY avgRating DESC")
    List<Object[]> getFeedbackByMenuItem(@Param("start") Instant start, 
                                          @Param("end") Instant end);
    
    // Filter feedback by period, rating, and menu item
    @Query("SELECT f FROM OrderItemFeedbackEntity f " +
           "WHERE f.createdAt BETWEEN :start AND :end " +
           "AND (:minRating IS NULL OR f.rating >= :minRating) " +
           "AND (:maxRating IS NULL OR f.rating <= :maxRating) " +
           "AND (:menuItemName IS NULL OR f.orderItem.name LIKE %:menuItemName%) " +
           "ORDER BY f.createdAt DESC")
    List<OrderItemFeedbackEntity> findFeedbackFiltered(@Param("start") Instant start, 
                                                        @Param("end") Instant end, 
                                                        @Param("minRating") Integer minRating, 
                                                        @Param("maxRating") Integer maxRating, 
                                                        @Param("menuItemName") String menuItemName);
    
    // Get rating distribution for a menu item in a period
    @Query("SELECT f.rating, COUNT(f) FROM OrderItemFeedbackEntity f " +
           "WHERE f.orderItem.name = :itemName " +
           "AND f.createdAt BETWEEN :start AND :end " +
           "GROUP BY f.rating ORDER BY f.rating")
    List<Object[]> getRatingDistributionForItemInPeriod(@Param("itemName") String itemName,
                                                         @Param("start") Instant start,
                                                         @Param("end") Instant end);
}
