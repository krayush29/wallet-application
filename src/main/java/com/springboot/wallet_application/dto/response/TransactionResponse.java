package com.springboot.wallet_application.dto.response;

import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private String username;
    private TransactionType transactionType;
    private double transactionAmount;
    private double currentBalance;
    private String message;

    public TransactionResponse(Transaction transaction, double currentBalance) {
        this.username = transaction.getFromUser().getUsername();
        this.transactionType = transaction.getType();
        this.transactionAmount = transaction.getAmount();
        this.currentBalance = currentBalance;
        this.message = transaction.getMessage();
    }
}
