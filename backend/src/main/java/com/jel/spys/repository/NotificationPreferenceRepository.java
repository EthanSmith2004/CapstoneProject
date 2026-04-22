package com.jel.spys.repository;

import com.jel.spys.entity.NotificationPreferenceEntity;
import com.jel.spys.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceEntity, Long> {
    
    Optional<NotificationPreferenceEntity> findByUser(UserEntity user);
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.user.id = :userId")
    Optional<NotificationPreferenceEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.pushEnabled = true")
    List<NotificationPreferenceEntity> findByPushEnabledTrue();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.emailEnabled = true")
    List<NotificationPreferenceEntity> findByEmailEnabledTrue();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.orderUpdates = true")
    List<NotificationPreferenceEntity> findByOrderUpdatesEnabled();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.menuUpdates = true")
    List<NotificationPreferenceEntity> findByMenuUpdatesEnabled();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.accountUpdates = true")
    List<NotificationPreferenceEntity> findByAccountUpdatesEnabled();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.promotional = true")
    List<NotificationPreferenceEntity> findByPromotionalEnabled();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.systemAnnouncements = true")
    List<NotificationPreferenceEntity> findBySystemAnnouncementsEnabled();
    
    @Query("SELECT np FROM NotificationPreferenceEntity np WHERE np.quietHoursStart IS NOT NULL AND np.quietHoursEnd IS NOT NULL")
    List<NotificationPreferenceEntity> findWithQuietHours();
    
    @Query("SELECT COUNT(np) FROM NotificationPreferenceEntity np WHERE np.pushEnabled = true")
    Long countByPushEnabledTrue();
    
    @Query("SELECT COUNT(np) FROM NotificationPreferenceEntity np WHERE np.emailEnabled = true")
    Long countByEmailEnabledTrue();
    
    @Query("SELECT COUNT(np) FROM NotificationPreferenceEntity np WHERE np.orderUpdates = true")
    Long countByOrderUpdatesEnabled();
    
    @Query("SELECT COUNT(np) FROM NotificationPreferenceEntity np WHERE np.menuUpdates = true")
    Long countByMenuUpdatesEnabled();
    
    @Query("SELECT COUNT(np) FROM NotificationPreferenceEntity np WHERE np.promotional = true")
    Long countByPromotionalEnabled();
}
