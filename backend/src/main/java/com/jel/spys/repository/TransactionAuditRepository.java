package com.jel.spys.repository;

import com.jel.spys.entity.TransactionAuditEntity;
import com.jel.spys.entity.TransactionAuditType;
import com.jel.spys.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionAuditRepository extends JpaRepository<TransactionAuditEntity, Long> {
    
    List<TransactionAuditEntity> findByUser(UserEntity user);
    
    List<TransactionAuditEntity> findByUserOrderByInitiatedAtDesc(UserEntity user);
    
    List<TransactionAuditEntity> findByType(TransactionAuditType type);
    
    List<TransactionAuditEntity> findByUserAndType(UserEntity user, TransactionAuditType type);
    
    @Query("SELECT ta FROM TransactionAuditEntity ta WHERE ta.initiatedAt BETWEEN :startDate AND :endDate ORDER BY ta.initiatedAt DESC")
    List<TransactionAuditEntity> findByInitiatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT ta FROM TransactionAuditEntity ta WHERE ta.user = :user AND ta.initiatedAt BETWEEN :startDate AND :endDate ORDER BY ta.initiatedAt DESC")
    List<TransactionAuditEntity> findByUserAndInitiatedAtBetween(@Param("user") UserEntity user, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT ta FROM TransactionAuditEntity ta WHERE ta.type = :type AND ta.initiatedAt BETWEEN :startDate AND :endDate ORDER BY ta.initiatedAt DESC")
    List<TransactionAuditEntity> findByTypeAndInitiatedAtBetween(@Param("type") TransactionAuditType type, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT ta FROM TransactionAuditEntity ta WHERE ta.loadedContent LIKE %:content% ORDER BY ta.initiatedAt DESC")
    List<TransactionAuditEntity> findByLoadedContentContaining(@Param("content") String content);
    
    @Query("SELECT COUNT(ta) FROM TransactionAuditEntity ta WHERE ta.user = :user")
    Long countByUser(@Param("user") UserEntity user);
    
    @Query("SELECT COUNT(ta) FROM TransactionAuditEntity ta WHERE ta.type = :type")
    Long countByType(@Param("type") TransactionAuditType type);
    
    @Query("SELECT COUNT(ta) FROM TransactionAuditEntity ta WHERE ta.user = :user AND ta.type = :type")
    Long countByUserAndType(@Param("user") UserEntity user, @Param("type") TransactionAuditType type);
    
    @Query("SELECT COUNT(ta) FROM TransactionAuditEntity ta WHERE ta.initiatedAt BETWEEN :startDate AND :endDate")
    Long countAuditsInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT ta FROM TransactionAuditEntity ta WHERE DATE(ta.initiatedAt) = DATE(:date) ORDER BY ta.initiatedAt DESC")
    List<TransactionAuditEntity> findAuditsForDate(@Param("date") Instant date);
    
    @Query("SELECT ta.type, COUNT(ta) FROM TransactionAuditEntity ta WHERE ta.initiatedAt BETWEEN :startDate AND :endDate GROUP BY ta.type")
    List<Object[]> getAuditTypeDistributionInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
