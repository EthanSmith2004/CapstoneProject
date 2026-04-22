package com.jel.spys.service;

import com.jel.spys.entity.TransactionAuditEntity;
import com.jel.spys.entity.UserEventEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.CompactUserDTO;
import com.jel.spys.model.TransactionAuditDTO;
import com.jel.spys.model.TransactionDTO;
import com.jel.spys.model.TransactionWithUserDTO;
import com.jel.spys.model.UserEventAuditDTO;
import com.jel.spys.repository.TransactionAuditRepository;
import com.jel.spys.repository.UserEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AuditService {

    @Autowired
    private TransactionAuditRepository transactionAuditRepository;

    @Autowired
    private UserEventRepository userEventRepository;

    /**
     * Get recent login events (last 50)
     */
    public List<UserEventAuditDTO> getRecentLogins() {
        log.info("Fetching recent login events");

        // Create pageable for top 50 results, sorted by time descending
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "time"));

        // Get all user events and filter for LOGIN type
        Page<UserEventEntity> events = userEventRepository.findAll(pageable);

        List<UserEventType> type = List.of(UserEventType.LOGIN, UserEventType.LOGOUT_EXPLICIT, UserEventType.LOGOUT_IMPLICIT);

        return events.getContent().stream()
                .filter(event -> type.contains(event.getEventType()))
                .map(UserEventAuditDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated user events (all types)
     */
    public Page<UserEventAuditDTO> getUserEventLogPaginated(int page, int size) {
        log.info("Fetching user events: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));
        Page<UserEventEntity> events = userEventRepository.findAll(pageable);
        
        return events.map(UserEventAuditDTO::new);
    }

    /**
     * Get paginated transaction audit records
     */
    public Page<TransactionAuditDTO> getTransactionAuditPaginated(int page, int size) {
        log.info("Fetching transaction audits: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "initiatedAt"));
        Page<TransactionAuditEntity> audits = transactionAuditRepository.findAll(pageable);
        
        return audits.map(this::convertToTransactionAuditDTO);
    }

    /**
     * Get transaction details with user info for a specific audit record
     */
    public List<TransactionWithUserDTO> getTransactionDetailsForAudit(Long auditId) {
        log.info("Fetching transaction details for audit ID: {}", auditId);
        
        TransactionAuditEntity audit = transactionAuditRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Transaction audit not found with ID: " + auditId));
        
        return audit.getTransactions().stream()
                .map(TransactionWithUserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Convert TransactionAuditEntity to TransactionAuditDTO
     */
    private TransactionAuditDTO convertToTransactionAuditDTO(TransactionAuditEntity entity) {
        List<TransactionDTO> transactions = entity.getTransactions().stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
        
        CompactUserDTO user = new CompactUserDTO(entity.getUser());
        
        return TransactionAuditDTO.builder()
                .id(entity.getId())
                .transactions(transactions)
                .transactionCount(entity.getTransactions().size())
                .compactUser(user)
                .loadedContent(entity.getLoadedContent())
                .transactionAuditType(entity.getType())
                .build();
    }
}
