package com.springboot.wallet_application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount should be greater than 0")
    private double amount;
}
