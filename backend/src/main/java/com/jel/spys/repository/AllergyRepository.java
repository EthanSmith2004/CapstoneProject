package com.jel.spys.repository;

import com.jel.spys.entity.AllergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllergyRepository extends JpaRepository<AllergyEntity, Long> {
    
    Optional<AllergyEntity> findByAllergy(String allergy);
    
    Boolean existsByAllergy(String allergy);
    
    @Query("SELECT a FROM AllergyEntity a WHERE LOWER(a.allergy) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<AllergyEntity> findByAllergyContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT a FROM AllergyEntity a ORDER BY a.allergy ASC")
    List<AllergyEntity> findAllOrderByAllergyAsc();
    
    @Query("SELECT DISTINCT a FROM AllergyEntity a JOIN UserProfileEntity up ON a MEMBER OF up.allergy WHERE up.id = :userProfileId")
    List<AllergyEntity> findByUserProfile(@Param("userProfileId") Long userProfileId);

    @Query("SELECT COUNT(up) FROM UserProfileEntity up JOIN up.allergy a WHERE a.id = :allergyId")
    Long countUsersWithAllergy(@Param("allergyId") Long allergyId);
}
