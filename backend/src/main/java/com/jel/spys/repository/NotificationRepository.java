package com.jel.spys.repository;

import com.jel.spys.entity.NotificationEntity;
import com.jel.spys.entity.NotificationStatus;
import com.jel.spys.entity.NotificationType;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    
    List<NotificationEntity> findByUser(UserEntity user);
    
    List<NotificationEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
    
    List<NotificationEntity> findByStatus(NotificationStatus status);
    
    List<NotificationEntity> findByType(NotificationType type);
    
    List<NotificationEntity> findByUserAndStatus(UserEntity user, NotificationStatus status);
    
    List<NotificationEntity> findByUserAndType(UserEntity user, NotificationType type);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.user = :user AND n.status IN :statuses ORDER BY n.createdAt DESC")
    List<NotificationEntity> findByUserAndStatusIn(@Param("user") UserEntity user, @Param("statuses") List<NotificationStatus> statuses);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.scheduledFor <= :now AND n.status = 'PENDING'")
    List<NotificationEntity> findPendingNotificationsDueForSending(@Param("now") Instant now);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.expiresAt < :now AND n.status IN ('PENDING', 'SENT')")
    List<NotificationEntity> findExpiredNotifications(@Param("now") Instant now);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.user = :user AND n.status = 'SENT' AND n.readAt IS NULL ORDER BY n.createdAt DESC")
    List<NotificationEntity> findUnreadNotificationsByUser(@Param("user") UserEntity user);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.user = :user AND n.status = 'READ' ORDER BY n.readAt DESC")
    List<NotificationEntity> findReadNotificationsByUser(@Param("user") UserEntity user);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.priority >= :priority ORDER BY n.priority DESC, n.createdAt DESC")
    List<NotificationEntity> findByPriorityGreaterThanEqual(@Param("priority") Integer priority);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.relatedOrder = :order")
    List<NotificationEntity> findByRelatedOrder(@Param("order") OrderEntity order);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<NotificationEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.retryCount >= :maxRetries AND n.status = 'FAILED'")
    List<NotificationEntity> findFailedNotificationsAboveRetryLimit(@Param("maxRetries") Integer maxRetries);
    
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = 'READ', n.readAt = :readAt WHERE n.id = :notificationId AND n.user = :user")
    void markAsRead(@Param("notificationId") Long notificationId, @Param("user") UserEntity user, @Param("readAt") Instant readAt);
    
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = 'read', n.readAt = :readAt WHERE n.user = :user AND n.status = 'SENT'")
    void markAllAsReadForUser(@Param("user") UserEntity user, @Param("readAt") Instant readAt);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.user = :user AND n.status = 'SENT' AND n.readAt IS NULL")
    Long countUnreadNotificationsByUser(@Param("user") UserEntity user);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.status = :status")
    Long countByStatus(@Param("status") NotificationStatus status);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.type = :type AND n.createdAt BETWEEN :startDate AND :endDate")
    Long countByTypeAndCreatedAtBetween(@Param("type") NotificationType type, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT n.type, COUNT(n) FROM NotificationEntity n WHERE n.createdAt BETWEEN :startDate AND :endDate GROUP BY n.type")
    List<Object[]> getNotificationTypeDistributionInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT n FROM NotificationEntity n WHERE DATE(n.createdAt) = DATE(:date) ORDER BY n.createdAt DESC")
    List<NotificationEntity> findNotificationsForDate(@Param("date") Instant date);
}
