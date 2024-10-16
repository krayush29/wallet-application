package com.springboot.wallet_application.dto.request;

import com.springboot.wallet_application.enums.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "Username cannot be null or empty")
    @Size(min = 5, message = "Username should have at least 5 characters")
    private String username;

    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 5, message = "Password should have at least 5 characters")
    private String password;

    private CurrencyType currencyType;

    public UserRegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.currencyType = CurrencyType.INR;
    }
}
