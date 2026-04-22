package com.jel.spys.repository;

import com.jel.spys.entity.*;

import jakarta.persistence.Tuple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    List<OrderItemEntity> findByOrder(OrderEntity order);
    
    List<OrderItemEntity> findByOrderOrderByCreatedAtAsc(OrderEntity order);
    
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.order = :order AND oi.name LIKE %:name%")
    List<OrderItemEntity> findByOrderAndNameContaining(@Param("order") OrderEntity order, @Param("name") String name);
    
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.name LIKE %:name%")
    List<OrderItemEntity> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItemEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT oi.name, SUM(oi.quantity) as totalQuantity FROM OrderItemEntity oi WHERE oi.createdAt BETWEEN :startDate AND :endDate GROUP BY oi.name ORDER BY totalQuantity DESC")
    List<Object[]> findPopularItemsByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT oi.name, SUM(oi.quantity) as totalQuantity FROM OrderItemEntity oi WHERE DATE(oi.createdAt) = DATE(:date) GROUP BY oi.name ORDER BY totalQuantity DESC")
    List<Object[]> findPopularItemsForDate(@Param("date") Instant date);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItemEntity oi WHERE oi.order = :order")
    Long sumQuantityByOrder(@Param("order") OrderEntity order);
    
    @Query("SELECT COUNT(oi) FROM OrderItemEntity oi WHERE oi.order = :order")
    Long countByOrder(@Param("order") OrderEntity order);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItemEntity oi WHERE oi.name = :itemName AND oi.createdAt BETWEEN :startDate AND :endDate")
    Long sumQuantityByItemNameAndDateRange(@Param("itemName") String itemName, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT oi FROM OrderItemEntity oi JOIN oi.order o WHERE o.user.id = :userId ORDER BY oi.createdAt DESC")
    List<OrderItemEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT oi FROM OrderItemEntity oi JOIN oi.order o WHERE o.user.id = :userId AND oi.id = :itemId ORDER BY oi.createdAt DESC")
    Optional<OrderItemEntity> findByUserIdAndId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Query("SELECT DISTINCT oi.name FROM OrderItemEntity oi ORDER BY oi.name ASC")
    List<String> findDistinctItemNames();

    @Query("SELECT oi FROM OrderItemEntity oi JOIN oi.order o WHERE o.user = :user AND oi.status = :status ORDER BY oi.createdAt DESC")
    List<OrderItemEntity> getUserOrdersByStatus(UserEntity user, OrderItemStatus status);

    @Query("SELECT oi.name, oi.status, oi.deliveryDate, SUM(oi.quantity) as count, SUM(oi.quantity * oi.price) " +
           "FROM OrderItemEntity oi " +
           "JOIN oi.order o " +
           "JOIN o.user u " +
           "JOIN UserProfileEntity up ON up.user = u " +
           "WHERE oi.deliveryDate BETWEEN :start AND :end " +
           "AND (:campusId IS NULL OR up.campus.id = :campusId) " +
           "AND (:residenceId IS NULL OR up.residence.id = :residenceId) " +
           "GROUP BY oi.deliveryDate, oi.name, oi.status")
    List<Tuple> getOrderStats(Instant start, Instant end, Long campusId, Long residenceId);

    @Query("SELECT oi FROM OrderItemEntity oi JOIN oi.order o WHERE o.user = :user AND oi.status IN :status ORDER BY oi.createdAt DESC")
    List<OrderItemEntity> getUserOrdersByStatusIn(UserEntity user, List<OrderItemStatus> status);

    @Query("SELECT oi.name, SUM(oi.quantity) as totalQuantity, SUM(oi.quantity * oi.price) as totalSales " +
           "FROM OrderItemEntity oi " +
           "WHERE oi.deliveryDate = :date AND oi.status = 'PAID' " +
           "GROUP BY oi.name " +
           "ORDER BY totalSales DESC")
    List<Tuple> getKitchenReportData(Instant date);
    
    @Query("SELECT oi.name, SUM(oi.quantity) as totalQuantity, SUM(oi.quantity * oi.price) as totalSales " +
           "FROM OrderItemEntity oi " +
           "JOIN oi.order o " +
           "JOIN o.user u " +
           "JOIN UserProfileEntity up ON up.user = u " +
           "WHERE oi.deliveryDate BETWEEN :start AND :end " +
           "AND oi.status = 'PAID' " +
           "AND (:campusId IS NULL OR up.campus.id = :campusId) " +
           "AND (:residenceId IS NULL OR up.residence.id = :residenceId) " +
           "GROUP BY oi.name " +
           "ORDER BY totalSales DESC")
    List<Tuple> getKitchenReportPeriodData(Instant start, Instant end, Long campusId, Long residenceId);
    
    @Query("SELECT "+
                "oi.deliveryDate, " +
                "oi.name, SUM(oi.quantity) as quantity, " +
                "u.firstName as firstName, " +
                "u.lastName as lastName, " +
                "up.credentialNumber as credentialNumber, " +
                "up.residence.residence as residenceName, " +
                "up.campus.campus as campusName " +
            "FROM OrderItemEntity oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN UserProfileEntity up ON up.user = u " +
            "WHERE oi.deliveryDate = :date AND oi.status IN :status " +
            "GROUP BY oi.deliveryDate, oi.name, u.firstName, u.lastName, up.credentialNumber, up.residence.residence, up.campus.campus " +
            "ORDER BY up.campus.campus ASC, up.residence.residence ASC, oi.deliveryDate ASC, oi.name ASC, up.credentialNumber ASC")
    List<Tuple> getDeliveryReportData(Instant date, List<OrderItemStatus> status);

    @Query("SELECT "+
                "oi.deliveryDate, " +
                "oi.name, SUM(oi.quantity) as quantity, " +
                "u.firstName as firstName, " +
                "u.lastName as lastName, " +
                "up.credentialNumber as credentialNumber, " +
                "up.residence.residence as residenceName, " +
                "up.campus.campus as campusName " +
            "FROM OrderItemEntity oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN UserProfileEntity up ON up.user = u " +
            "WHERE oi.deliveryDate BETWEEN :start AND :end " +
            "AND oi.status IN :status " +
            "AND (:campusId IS NULL OR up.campus.id = :campusId) " +
            "AND (:residenceId IS NULL OR up.residence.id = :residenceId) " +
            "GROUP BY oi.deliveryDate, oi.name, u.firstName, u.lastName, up.credentialNumber, up.residence.residence, up.campus.campus " +
            "ORDER BY up.campus.campus ASC, up.residence.residence ASC, oi.deliveryDate ASC, oi.name ASC, up.credentialNumber ASC")
    List<Tuple> getDeliveryReportPeriodData(Instant start, Instant end, List<OrderItemStatus> status, Long campusId, Long residenceId);

    @Modifying
    @Query("UPDATE OrderItemEntity oi SET oi.status = :status WHERE oi.deliveryDate = :deliveryDate AND oi.name = :itemName AND oi.status = :previousStatus")
    void bulkUpdateOrderStatuses(Instant deliveryDate, String itemName, OrderItemStatus status,
            OrderItemStatus previousStatus);

    // Methods for retrieving order items for notification purposes before bulk updates
    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.deliveryDate = :deliveryDate AND oi.name = :itemName AND oi.status = :status")
    List<OrderItemEntity> findByDeliveryDateAndNameAndStatus(@Param("deliveryDate") Instant deliveryDate, 
                                                             @Param("itemName") String itemName, 
                                                             @Param("status") OrderItemStatus status);

    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.deliveryDate = :deliveryDate AND oi.status = :status")
    List<OrderItemEntity> findByDeliveryDateAndStatus(@Param("deliveryDate") Instant deliveryDate, 
                                                      @Param("status") OrderItemStatus status);

    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.name = :itemName AND oi.status = :status")
    List<OrderItemEntity> findByNameAndStatus(@Param("itemName") String itemName, 
                                             @Param("status") OrderItemStatus status);

    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.status = :status")
    List<OrderItemEntity> findByStatus(@Param("status") OrderItemStatus status);

    // Delivery-specific queries
    @Query("SELECT oi FROM OrderItemEntity oi " +
           "JOIN oi.order o " +
           "WHERE o.user = :user AND oi.status = :status " +
           "ORDER BY oi.deliveryDate ASC")
    List<OrderItemEntity> findPendingDeliveryItemsForUser(@Param("user") UserEntity user,
                                                          @Param("status") OrderItemStatus status);

    @Query("SELECT oi FROM OrderItemEntity oi " +
           "WHERE oi.status = :status " +
           "AND oi.deliveryDate BETWEEN :startDate AND :endDate " +
           "ORDER BY oi.deliveryDate ASC")
    List<OrderItemEntity> findByStatusAndDeliveryDateBetween(@Param("status") OrderItemStatus status,
                                                             @Param("startDate") Instant startDate,
                                                             @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(oi) FROM OrderItemEntity oi " +
           "WHERE oi.status = :status " +
           "AND oi.deliveryDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndDeliveryDateBetween(@Param("status") OrderItemStatus status,
                                            @Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(oi) FROM OrderItemEntity oi " +
           "WHERE oi.status = :status " +
           "AND oi.dateTimeDelivered BETWEEN :startDate AND :endDate")
    Long countByStatusAndDeliveredDateBetween(@Param("status") OrderItemStatus status,
                                            @Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(oi) FROM OrderItemEntity oi " +
            "WHERE oi.status = :status ")
    Long countByStatus(@Param("status") OrderItemStatus status);

    // Finance statistics queries
    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItemEntity oi " +
           "WHERE oi.createdAt BETWEEN :startDate AND :endDate " +
           "AND oi.status NOT IN ('CANCELLED', 'REFUNDED', 'DELIVERED') " +
           "AND oi.editBy > :now")
    BigDecimal sumPendingRevenueInDateRange(@Param("startDate") Instant startDate,
                                           @Param("endDate") Instant endDate,
                                           @Param("now") Instant now);
    
    // Menu item statistics queries
    @Query("SELECT oi.name, COUNT(DISTINCT oi.order.id), SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
           "FROM OrderItemEntity oi " +
           "WHERE oi.status NOT IN ('CANCELLED', 'REFUNDED') " +
           "GROUP BY oi.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findMenuItemStatistics();
    
    @Query("SELECT oi.name, COUNT(DISTINCT oi.order.id), SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
           "FROM OrderItemEntity oi " +
           "WHERE oi.status NOT IN ('CANCELLED', 'REFUNDED') " +
           "AND oi.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY oi.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findMenuItemStatisticsByDateRange(@Param("startDate") Instant startDate,
                                                      @Param("endDate") Instant endDate);
}
