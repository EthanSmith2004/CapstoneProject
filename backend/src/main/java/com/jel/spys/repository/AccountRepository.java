package com.jel.spys.repository;

import com.jel.spys.entity.AccountEntity;
import com.jel.spys.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    
    Optional<AccountEntity> findByUserProfile(UserProfileEntity userProfile);
    
    @Query("SELECT a FROM AccountEntity a WHERE a.userProfile.user.id = :userId")
    Optional<AccountEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM AccountEntity a WHERE a.userProfile.credentialNumber = :credentialNumber")
    Optional<AccountEntity> findByCredentialNumber(@Param("credentialNumber") String credentialNumber);
    
    @Query("SELECT a FROM AccountEntity a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<AccountEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT a FROM AccountEntity a ORDER BY a.createdAt DESC")
    List<AccountEntity> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT a FROM AccountEntity a WHERE a.userProfile.campus.id = :campusId")
    List<AccountEntity> findByCampusId(@Param("campusId") Long campusId);
    
    @Query("SELECT a FROM AccountEntity a WHERE a.userProfile.residence.id = :residenceId")
    List<AccountEntity> findByResidenceId(@Param("residenceId") Long residenceId);

    @Query("SELECT COUNT(a) FROM AccountEntity a")
    Long countAllAccounts();
    
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.userProfile.campus.id = :campusId")
    Long countAccountsByCampus(@Param("campusId") Long campusId);
    
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.userProfile.residence.id = :residenceId")
    Long countAccountsByResidence(@Param("residenceId") Long residenceId);

    @Query("SELECT a.runningBalance from TransactionEntity a WHERE a.account = :account ORDER BY a.transactionDate DESC LIMIT 1")
    Optional<BigDecimal> getAccountBalance(@Param("account") AccountEntity account);
}
