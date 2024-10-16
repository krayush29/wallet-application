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
    private double amount;
    private String message;

    public TransactionResponse(Transaction transaction) {
        this.username = transaction.getFromUser().getUsername();
        this.transactionType = transaction.getType();
        this.amount = transaction.getAmount();
        this.message = transaction.getMessage();
    }
}
