package com.springboot.wallet_application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestBody {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
