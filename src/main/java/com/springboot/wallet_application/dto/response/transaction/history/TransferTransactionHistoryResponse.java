package com.springboot.wallet_application.dto.response.transaction.history;

import com.springboot.wallet_application.entity.TransferTransaction;
import com.springboot.wallet_application.entity.User;
import lombok.Getter;

@Getter
public class TransferTransactionHistoryResponse extends TransactionHistoryResponse {
    private final String senderUser;
    private final String recipientUsername;

    public TransferTransactionHistoryResponse(TransferTransaction transaction, User senderUser, User recipientUser) {
        super(transaction, senderUser.getWallet());
        this.senderUser = senderUser.getUsername();
        this.recipientUsername = recipientUser.getUsername();
    }
}
