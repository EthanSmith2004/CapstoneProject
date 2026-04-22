package com.jel.spys.repository;

import com.jel.spys.entity.MenuItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {

    List<MenuItemEntity> findByReleaseDateAfterOrderByReleaseDateAsc(Instant releaseDate);

    List<MenuItemEntity> findByReleaseDateBeforeOrderByDeliveryDateAsc(Instant releaseDate);

    List<MenuItemEntity> findByReleaseDateBeforeAndEditByAfterOrderByDeliveryDateAsc(Instant releaseDate,
            Instant editBy);

    Page<MenuItemEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    @Query("SELECT m FROM MenuItemEntity m WHERE m.releaseDate <= :now AND (m.editBy IS NULL OR m.editBy >= :now) ORDER BY m.deliveryDate ASC")
    List<MenuItemEntity> findCurrentlyAvailableMenuItems(@Param("now") Instant now);

    @Query("SELECT m FROM MenuItemEntity m WHERE m.deliveryDate BETWEEN :startDate AND :endDate ORDER BY m.deliveryDate ASC")
    List<MenuItemEntity> findByDeliveryDateBetween(@Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    List<MenuItemEntity> findByOrderByDeliveryDateAsc();
    
    // New queries for the refactored menu endpoints
    
    // Items with null delivery, release, edit and order dates (draft items)
    @Query("SELECT m FROM MenuItemEntity m WHERE m.deliveryDate IS NULL AND m.releaseDate IS NULL AND m.editBy IS NULL AND m.orderBy IS NULL ORDER BY m.createdAt DESC")
    List<MenuItemEntity> findDraftItems();
    
    // Released items where orderBy is not past (current menu)
    @Query("SELECT m FROM MenuItemEntity m WHERE m.releaseDate IS NOT NULL AND m.deliveryDate IS NOT NULL AND m.editBy IS NOT NULL AND m.orderBy IS NOT NULL AND m.orderBy >= :now ORDER BY m.deliveryDate ASC")
    List<MenuItemEntity> findCurrentMenuItems(@Param("now") Instant now);
    
    // Items where orderBy is past (historic menu)
    @Query("SELECT m FROM MenuItemEntity m WHERE m.orderBy IS NOT NULL AND m.orderBy < :now ORDER BY m.orderBy DESC")
    List<MenuItemEntity> findHistoricMenuItems(@Param("now") Instant now);
}
