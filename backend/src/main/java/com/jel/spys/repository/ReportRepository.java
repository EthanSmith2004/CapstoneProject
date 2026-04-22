package com.jel.spys.repository;

import com.jel.spys.entity.ReportEntity;
import com.jel.spys.entity.ReportStatus;
import com.jel.spys.entity.ReportType;
import com.jel.spys.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    
    List<ReportEntity> findByRequestedBy(UserEntity requestedBy);
    
    List<ReportEntity> findByRequestedByOrderByCreatedAtDesc(UserEntity requestedBy);
    
    List<ReportEntity> findByStatus(ReportStatus status);
    
    List<ReportEntity> findByType(ReportType type);
    
    List<ReportEntity> findByStatusAndType(ReportStatus status, ReportType type);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.status = :status ORDER BY r.createdAt ASC")
    List<ReportEntity> findByStatusOrderByCreatedAtAsc(@Param("status") ReportStatus status);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.status IN ('REQUESTED', 'PROCESSING') ORDER BY r.createdAt ASC")
    List<ReportEntity> findPendingReports();
    
    @Query("SELECT r FROM ReportEntity r WHERE r.status = 'COMPLETED' AND r.lastDownloadedAt IS NULL")
    List<ReportEntity> findCompletedUndownloadedReports();
    
    @Query("SELECT r FROM ReportEntity r WHERE r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<ReportEntity> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.requestedBy = :user AND r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<ReportEntity> findByRequestedByAndCreatedAtBetween(@Param("user") UserEntity user, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.dateFrom >= :fromDate AND r.dateTo <= :toDate")
    List<ReportEntity> findByDateRangeWithin(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.dateFrom <= :date AND r.dateTo >= :date")
    List<ReportEntity> findByDateRangeContaining(@Param("date") Instant date);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.status = 'PROCESSING' AND r.processingStartedAt < :cutoffTime")
    List<ReportEntity> findStuckProcessingReports(@Param("cutoffTime") Instant cutoffTime);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.status = 'FAILED' ORDER BY r.updatedAt DESC")
    List<ReportEntity> findFailedReports();
    
    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReportStatus status);
    
    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.type = :type")
    Long countByType(@Param("type") ReportType type);
    
    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.requestedBy = :user")
    Long countByRequestedBy(@Param("user") UserEntity user);
    
    @Query("SELECT r.type, COUNT(r) FROM ReportEntity r WHERE r.createdAt BETWEEN :startDate AND :endDate GROUP BY r.type")
    List<Object[]> getReportTypeDistributionInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT r.status, COUNT(r) FROM ReportEntity r WHERE r.createdAt BETWEEN :startDate AND :endDate GROUP BY r.status")
    List<Object[]> getReportStatusDistributionInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
