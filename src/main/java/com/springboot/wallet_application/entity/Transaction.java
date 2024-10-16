package com.springboot.wallet_application.entity;

import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "from_user_id", referencedColumnName = "user_id", nullable = false)
    private User fromUser;

    @OneToOne
    @JoinColumn(name = "to_user_id", referencedColumnName = "user_id", nullable = false)
    private User toUser;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private double amount;

    private String message;

    public Transaction(User fromUser, User toUser, TransactionType type, double amount, CurrencyType paymentCurrency) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.type = type;
        this.amount = amount;
        this.message = getMessage(fromUser, toUser, type, amount, paymentCurrency);
    }

    public Transaction(User fromUser, TransactionType type, double amount, CurrencyType paymentCurrency) {
        this.fromUser = fromUser;
        this.toUser = fromUser;
        this.type = type;
        this.amount = amount;
        this.message = getMessage(fromUser, fromUser, type, amount, paymentCurrency);
    }

    private String getMessage(User fromUser, User toUser, TransactionType type, double amount, CurrencyType paymentCurrency) {

        if (TransactionType.DEPOSIT.equals(type)) {
            return String.format("Deposited amount %.2f (%s) to %s", amount, paymentCurrency, fromUser.getUsername());
        } else if (TransactionType.WITHDRAWAL.equals(type)) {
            return String.format("Deducted amount %.2f (%s) from %s", amount, paymentCurrency, toUser.getUsername());
        } else {
            return String.format("Transferred amount %.2f (%s) to %s from %s",
                    amount,
                    toUser.getWallet().getCurrencyType(),
                    toUser.getUsername(),
                    fromUser.getUsername());
        }
    }
}
