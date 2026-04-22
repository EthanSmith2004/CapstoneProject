package com.jel.spys.model;

import com.jel.spys.entity.TransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    Long id;
    BigDecimal debit;
    BigDecimal credit;
    BigDecimal runningBalance;
    Instant transactionDate;
    String description;

    public TransactionDTO(TransactionEntity transaction) {
        this.id = transaction.getId();
        this.debit = transaction.getDebitAmount();
        this.credit = transaction.getCreditAmount();
        this.runningBalance = transaction.getRunningBalance();
        this.transactionDate = transaction.getTransactionDate();
        this.description = transaction.getDescription();
    }
}
