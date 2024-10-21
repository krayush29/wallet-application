package com.springboot.wallet_application.dto.request;

import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.InvalidTransactionException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@NoArgsConstructor
public class TransactionRequest {

    private TransactionType transactionType;

    @Setter
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount should be greater than 0")
    private double amount;

    @Setter
    String recipientUsername;

    public TransactionRequest(String transactionType, double amount, String recipientUsername) {
        ValidateTransactionType(transactionType);
        this.transactionType = TransactionType.valueOf(transactionType);
        this.amount = amount;
        this.recipientUsername = recipientUsername;
    }

    public TransactionRequest(String transactionType, double amount) {
        ValidateTransactionType(transactionType);
        this.transactionType = TransactionType.valueOf(transactionType);
        this.amount = amount;
        this.recipientUsername = null;
    }

    public void setTransactionType(String transactionType) {
        ValidateTransactionType(transactionType);
        this.transactionType = TransactionType.valueOf(transactionType);
    }

    private void ValidateTransactionType(String transactionType) {
        for (TransactionType t : TransactionType.values()) {
            if (t.name().equals(transactionType)) {
                return;
            }
        }
        throw new InvalidTransactionException("Invalid transaction type: " + transactionType + ". Please provide a valid transaction type." + Arrays.toString(TransactionType.values()));
    }
}
