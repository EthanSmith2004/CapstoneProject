package com.jel.spys.repository;

import com.jel.spys.entity.MenuTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuTemplateRepository extends JpaRepository<MenuTemplateEntity, Long> {

    @Query("SELECT DISTINCT m.presetName FROM MenuTemplateEntity m ORDER BY m.presetName")
    List<String> findDistinctPresetNames();

    @Query("SELECT m FROM MenuTemplateEntity m WHERE m.presetName = :presetName ORDER BY m.deliveryOffsetMinutes")
    List<MenuTemplateEntity> findByPresetNameOrderByDeliveryOffset(@Param("presetName") String presetName);

    @Modifying
    @Query("DELETE FROM MenuTemplateEntity m WHERE m.presetName = :presetName")
    void deleteByPresetName(@Param("presetName") String presetName);

    @Query("SELECT m.presetName, COUNT(m), MAX(m.updatedAt) FROM MenuTemplateEntity m GROUP BY m.presetName ORDER BY m.presetName")
    List<Object[]> findPresetNamesWithMetadata();
}