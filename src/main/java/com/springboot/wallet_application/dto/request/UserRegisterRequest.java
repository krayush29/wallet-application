package com.springboot.wallet_application.dto.request;

import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.InvalidCurrencyException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;


@Getter
@NoArgsConstructor
public class UserRegisterRequest {

    @Setter
    @NotNull(message = "Username cannot be null")
    @Size(min = 5, message = "Username should have at least 5 characters")
    private String username;

    @Setter
    @NotNull(message = "Username cannot be null")
    @Size(min = 5, message = "Password should have at least 5 characters")
    private String password;

    private CurrencyType currencyType;

    public UserRegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.currencyType = CurrencyType.INR;
    }

    public UserRegisterRequest(String username, String password, String currencyType) {
        this.username = username;
        this.password = password;
        isCurrencyTypeValid(currencyType);
        this.currencyType = CurrencyType.valueOf(currencyType);
    }

    //For JSON Parsing
    public void setCurrencyType(String currencyType) {
        isCurrencyTypeValid(currencyType);
        this.currencyType = CurrencyType.valueOf(currencyType);
    }

    private void isCurrencyTypeValid(String currencyType) {
        for (CurrencyType c : CurrencyType.values()) {
            if (c.name().equals(currencyType)) {
                return;
            }
        }
        throw new InvalidCurrencyException("Invalid currency type: " + currencyType + ". Please provide a valid currency type." + Arrays.toString(CurrencyType.values()));
    }
}
