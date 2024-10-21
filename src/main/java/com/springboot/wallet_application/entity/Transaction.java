package com.springboot.wallet_application.entity;

import com.springboot.wallet_application.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String message;

    public Transaction(TransactionType transactionType, double amount) {
        this.type = transactionType;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
}
