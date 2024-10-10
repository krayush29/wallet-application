package com.springboot.wallet_application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Transaction {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private double amount;
}
