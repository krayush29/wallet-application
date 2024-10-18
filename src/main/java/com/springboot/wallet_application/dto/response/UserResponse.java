package com.springboot.wallet_application.dto.response;


import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String username;
    private double balance;
    private CurrencyType currency;

    public UserResponse(String username, Wallet wallet) {
        this.username = username;
        this.balance = wallet.getBalance();
        this.currency = wallet.getCurrencyType();
    }
}
