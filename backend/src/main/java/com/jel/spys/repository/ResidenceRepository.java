package com.jel.spys.repository;

import com.jel.spys.entity.ResidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResidenceRepository extends JpaRepository<ResidenceEntity, Long> {
    
    Optional<ResidenceEntity> findByResidence(String residence);
    
    Boolean existsByResidence(String residence);
    
    @Query("SELECT r FROM ResidenceEntity r WHERE LOWER(r.residence) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ResidenceEntity> findByResidenceContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT r FROM ResidenceEntity r ORDER BY r.residence ASC")
    List<ResidenceEntity> findAllOrderByResidenceAsc();
    
    @Query("SELECT r FROM ResidenceEntity r WHERE r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<ResidenceEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(up) FROM UserProfileEntity up WHERE up.residence.id = :residenceId")
    Long countUsersByResidence(@Param("residenceId") Long residenceId);
    
    @Query("SELECT r.residence, COUNT(up) FROM ResidenceEntity r LEFT JOIN UserProfileEntity up ON r = up.residence GROUP BY r.residence ORDER BY COUNT(up) DESC")
    List<Object[]> getResidenceUserCounts();
}
