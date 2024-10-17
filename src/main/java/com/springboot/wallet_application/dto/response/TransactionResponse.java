package com.springboot.wallet_application.dto.response;

import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private String username;
    private TransactionType transactionType;
    private double transactionAmount;
    private double currentBalance;
    private CurrencyType currencyType;
    private String message;
    private String timestamp;

    public TransactionResponse(Transaction transaction, Wallet wallet) {
        this.username = transaction.getFromUser().getUsername();
        this.transactionType = transaction.getType();
        this.transactionAmount = transaction.getAmount();
        this.currentBalance = wallet.getBalance();
        this.currencyType = wallet.getCurrencyType();
        this.message = transaction.getMessage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = transaction.getTimestamp().format(formatter);
    }
}
