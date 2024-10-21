package com.springboot.wallet_application.dto.response.transaction.history;

import com.springboot.wallet_application.entity.SelfTransaction;
import com.springboot.wallet_application.entity.User;
import lombok.Getter;

@Getter
public class SelfTransactionHistoryResponse extends TransactionHistoryResponse {
    private final String username;

    public SelfTransactionHistoryResponse(SelfTransaction selfTransaction, User user) {
        super(selfTransaction, user.getWallet());
        this.username = user.getUsername();
    }
}
