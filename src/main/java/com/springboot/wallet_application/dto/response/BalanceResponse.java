package com.springboot.wallet_application.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceResponse {
    private String username;
    private double balance;
}
