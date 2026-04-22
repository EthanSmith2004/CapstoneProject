package com.jel.spys.model;

import com.jel.spys.entity.TransactionEntity;
import com.jel.spys.entity.UserEntity;
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
public class AdminTransactionDTO {
    Long id;
    BigDecimal debit;
    BigDecimal credit;
    BigDecimal runningBalance;
    Instant transactionDate;
    String description;
    UserWithProfileDTO user;

    public AdminTransactionDTO(TransactionEntity transaction) {
        this.id = transaction.getId();
        this.debit = transaction.getDebitAmount();
        this.credit = transaction.getCreditAmount();
        this.runningBalance = transaction.getRunningBalance();
        this.transactionDate = transaction.getTransactionDate();
        this.description = transaction.getDescription();
        this.user = new UserWithProfileDTO(transaction.getAccount().getUserProfile());
    }
}
