package com.springboot.wallet_application.dto.response.transaction;

import com.springboot.wallet_application.entity.SelfTransaction;
import com.springboot.wallet_application.entity.User;
import lombok.Getter;

@Getter
public class SelfTransactionResponse extends TransactionResponse {
    private final String username;

    public SelfTransactionResponse(SelfTransaction selfTransaction, User user) {
        super(selfTransaction, user.getWallet());
        this.username = user.getUsername();
    }
}
