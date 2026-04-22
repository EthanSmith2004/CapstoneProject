package com.jel.spys.service;

import com.jel.spys.entity.TransactionAuditEntity;
import com.jel.spys.entity.TransactionAuditType;
import com.jel.spys.entity.TransactionEntity;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.repository.TransactionAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class TransactionAuditService {

    @Autowired
    private TransactionAuditRepository transactionAuditRepository;

    @Autowired
    private ClockService clockService;

    /**
     * Create a transaction audit record with linked transactions
     */
    public TransactionAuditEntity createAuditRecord(
            UserEntity user,
            TransactionAuditType type,
            String description,
            List<TransactionEntity> transactions) {
        
        log.info("Creating transaction audit: type={}, user={}, description={}, transactionCount={}", 
                type, user.getEmail(), description, transactions != null ? transactions.size() : 0);

        TransactionAuditEntity audit = new TransactionAuditEntity(
                user,
                clockService.now(),
                description,
                type
        );

        // Save the audit record first to get an ID
        audit = transactionAuditRepository.save(audit);

        // Link transactions if provided
        if (transactions != null && !transactions.isEmpty()) {
            // The transactions list in the entity is final and initialized as empty ArrayList
            // We need to add transactions to it
            audit.getTransactions().addAll(transactions);
            audit = transactionAuditRepository.save(audit);
            log.info("Linked {} transactions to audit record {}", transactions.size(), audit.getId());
        }

        return audit;
    }

    /**
     * Create a transaction audit record with a single transaction
     */
    public TransactionAuditEntity createAuditRecord(
            UserEntity user,
            TransactionAuditType type,
            String description,
            TransactionEntity transaction) {
        
        return createAuditRecord(user, type, description, 
                transaction != null ? List.of(transaction) : null);
    }

    /**
     * Create a transaction audit record without any transactions
     */
    public TransactionAuditEntity createAuditRecord(
            UserEntity user,
            TransactionAuditType type,
            String description) {
        
        return createAuditRecord(user, type, description, (List<TransactionEntity>) null);
    }
}
