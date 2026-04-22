package com.jel.spys.repository;

import com.jel.spys.entity.CampusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<CampusEntity, Long> {
    
    Optional<CampusEntity> findByCampus(String campus);
    
    Boolean existsByCampus(String campus);
    
    @Query("SELECT c FROM CampusEntity c WHERE LOWER(c.campus) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CampusEntity> findByCampusContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT c FROM CampusEntity c ORDER BY c.campus ASC")
    List<CampusEntity> findAllOrderByCampusAsc();
    
    @Query("SELECT c FROM CampusEntity c WHERE c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<CampusEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(up) FROM UserProfileEntity up WHERE up.campus.id = :campusId")
    Long countUsersByCampus(@Param("campusId") Long campusId);
    
    @Query("SELECT c.campus, COUNT(up) FROM CampusEntity c LEFT JOIN UserProfileEntity up ON c = up.campus GROUP BY c.campus ORDER BY COUNT(up) DESC")
    List<Object[]> getCampusUserCounts();
}
