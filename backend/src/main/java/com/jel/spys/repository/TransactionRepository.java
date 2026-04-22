package com.jel.spys.repository;

import com.jel.spys.entity.TransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByTransactionDateBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.transactionDate >= :fromDate ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByTransactionDateAfter(@Param("fromDate") Instant fromDate);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.debitAmount > 0 ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findDebitTransactions();
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.creditAmount > 0 ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findCreditTransactions();
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.debitAmount >= :amount ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findDebitTransactionsAboveAmount(@Param("amount") BigDecimal amount);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.creditAmount >= :amount ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findCreditTransactionsAboveAmount(@Param("amount") BigDecimal amount);
    
    @Query("SELECT t FROM TransactionEntity t WHERE t.description LIKE %:description% ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findByDescriptionContaining(@Param("description") String description);
    
    @Query("SELECT SUM(t.debitAmount) FROM TransactionEntity t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumDebitAmountInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT SUM(t.creditAmount) FROM TransactionEntity t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCreditAmountInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    Long countTransactionsInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.debitAmount > 0 AND t.transactionDate BETWEEN :startDate AND :endDate")
    Long countDebitTransactionsInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.creditAmount > 0 AND t.transactionDate BETWEEN :startDate AND :endDate")
    Long countCreditTransactionsInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT t FROM TransactionEntity t WHERE DATE(t.transactionDate) = DATE(:date) ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findTransactionsForDate(@Param("date") Instant date);
    
    @Query("SELECT AVG(t.debitAmount) FROM TransactionEntity t WHERE t.debitAmount > 0 AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getAverageDebitAmountInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
    
    @Query("SELECT AVG(t.creditAmount) FROM TransactionEntity t WHERE t.creditAmount > 0 AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getAverageCreditAmountInDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT t FROM TransactionEntity t where t.account.id = :id")
    List<TransactionEntity> getByAccountId(@Param("id") Long id, Pageable pageable);

    // Finance statistics - refund amount
    @Query("SELECT SUM(t.creditAmount) FROM TransactionEntity t " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND (LOWER(t.description) LIKE '%refund%' OR LOWER(t.description) LIKE '%terugbetaling%')")
    BigDecimal sumRefundAmountInDateRange(@Param("startDate") Instant startDate, 
                                         @Param("endDate") Instant endDate);
}
