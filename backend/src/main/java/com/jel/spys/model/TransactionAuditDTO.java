package com.jel.spys.model;

import com.jel.spys.entity.TransactionAuditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAuditDTO {
    private Long id;
    private List<TransactionDTO> transactions;
    private Integer transactionCount;
    private CompactUserDTO compactUser;
    private String loadedContent;
    private TransactionAuditType transactionAuditType;
}
