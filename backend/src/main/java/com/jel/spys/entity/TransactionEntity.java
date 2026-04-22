package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="transaction", indexes = {
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_transaction_debit", columnList = "debit"),
    @Index(name = "idx_transaction_credit", columnList = "credit")
})
@Getter
@Immutable
@RequiredArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "debit", precision =  19, scale = 4)
    private final BigDecimal debitAmount;

    @Column(nullable = false, name = "credit", precision = 19, scale = 4)
    private final BigDecimal creditAmount;

    @Column(nullable = false, name = "running_balance", precision = 19, scale = 4)
    private final BigDecimal runningBalance;

    @Column(nullable = false, name = "transaction_date")
    private final Instant transactionDate;

    @Column(nullable = false)
    private final String description;

    @ManyToOne
    @Setter
    private AccountEntity account;

    protected TransactionEntity() {
        debitAmount = BigDecimal.ZERO;
        creditAmount = BigDecimal.ZERO;
        runningBalance = BigDecimal.ZERO;
        transactionDate = Instant.now();
        description = "";
    }
}
