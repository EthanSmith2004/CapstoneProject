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
public class TransactionWithUserDTO {
    private Long id;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal runningBalance;
    private Instant transactionDate;
    private String description;
    private String accountOwnerName;
    private String accountOwnerEmail;

    public TransactionWithUserDTO(TransactionEntity transaction) {
        this.id = transaction.getId();
        this.debit = transaction.getDebitAmount();
        this.credit = transaction.getCreditAmount();
        this.runningBalance = transaction.getRunningBalance();
        this.transactionDate = transaction.getTransactionDate();
        this.description = transaction.getDescription();
        
        // Get account owner info
        if (transaction.getAccount() != null && 
            transaction.getAccount().getUserProfile() != null &&
            transaction.getAccount().getUserProfile().getUser() != null) {
            var user = transaction.getAccount().getUserProfile().getUser();
            this.accountOwnerName = user.getFirstName() + " " + user.getLastName();
            this.accountOwnerEmail = user.getEmail();
        }
    }
}
