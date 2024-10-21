package com.springboot.wallet_application.entity;

import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.InvalidTransactionException;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "self_transactions")
public class SelfTransaction extends Transaction {
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    public SelfTransaction(User user, TransactionType transactionType, double amount) {
        super(transactionType, amount);
        this.user = user;
        String message = getMessage(user, transactionType, amount, user.getWallet().getCurrencyType());
        this.setMessage(message);
    }

    private String getMessage(User user, TransactionType type, double amount, CurrencyType paymentCurrency) {

        if (TransactionType.DEPOSIT.equals(type)) {
            return String.format("Deposited amount %.2f (%s) to %s", amount, paymentCurrency, user.getUsername());
        } else if (TransactionType.WITHDRAWAL.equals(type)) {
            return String.format("Deducted amount %.2f (%s) from %s", amount, paymentCurrency, user.getUsername());
        } else {
            throw new InvalidTransactionException("Invalid self transaction type: " + type + ". Please provide a valid self transaction type.");
        }
    }
}
