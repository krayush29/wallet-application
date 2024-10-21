package com.springboot.wallet_application.entity;

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
@Table(name = "transfer_transactions")
public class TransferTransaction extends Transaction {

    @ManyToOne
    @JoinColumn(name = "from_user_id", referencedColumnName = "user_id", nullable = false)
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", referencedColumnName = "user_id", nullable = false)
    private User toUser;

    public TransferTransaction(User fromUser, User toUser, TransactionType transactionType, double amount) {
        super(transactionType, amount);
        this.fromUser = fromUser;
        this.toUser = toUser;
        String message = getMessage(fromUser, toUser, transactionType, amount);
        this.setMessage(message);
    }

    private String getMessage(User fromUser, User toUser, TransactionType type, double amount) {
        if (TransactionType.TRANSFER.equals(type)) {
            return String.format("Transferred amount %.2f (%s) to %s from %s",
                    amount,
                    fromUser.getWallet().getCurrencyType(),
                    toUser.getUsername(),
                    fromUser.getUsername());
        }

        throw new InvalidTransactionException("Invalid transfer transaction type: " + type + ". Please provide a valid transfer transaction type.");
    }

}
