package com.springboot.wallet_application.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyRequest {

    String recipientUsername;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount should be greater than 0")
    private double transferAmount;
}
