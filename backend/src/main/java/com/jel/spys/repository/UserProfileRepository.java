package com.jel.spys.repository;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserProfileEntity;
import com.jel.spys.entity.CampusEntity;
import com.jel.spys.entity.ResidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    
    Optional<UserProfileEntity> findByUser(UserEntity user);
    
    Optional<UserProfileEntity> findByCredentialNumber(String credentialNumber);
    
    Boolean existsByCredentialNumber(String credentialNumber);
    
    List<UserProfileEntity> findByCampus(CampusEntity campus);
    
    List<UserProfileEntity> findByResidence(ResidenceEntity residence);
    
    List<UserProfileEntity> findByCampusAndResidence(CampusEntity campus, ResidenceEntity residence);
    
    @Query("SELECT up FROM UserProfileEntity up WHERE up.user.id = :userId")
    Optional<UserProfileEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT up FROM UserProfileEntity up JOIN up.allergy a WHERE a.id = :allergyId")
    List<UserProfileEntity> findByAllergyId(@Param("allergyId") Long allergyId);
    
    @Query("SELECT COUNT(up) FROM UserProfileEntity up WHERE up.campus = :campus")
    Long countByCampus(@Param("campus") CampusEntity campus);
    
    @Query("SELECT COUNT(up) FROM UserProfileEntity up WHERE up.residence = :residence")
    Long countByResidence(@Param("residence") ResidenceEntity residence);
}
