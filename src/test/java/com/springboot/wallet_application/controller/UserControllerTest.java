package com.springboot.wallet_application.controller;


import com.springboot.wallet_application.dto.response.BalanceResponse;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBalance() throws Exception {
        BalanceResponse balanceResponse = new BalanceResponse("username", 100.0, CurrencyType.INR);
        when(userService.getBalance()).thenReturn(balanceResponse);

        mockMvc.perform(get("/users/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(100.0)));
    }

    @Test
    void testBalanceByUserId() throws Exception {
        BalanceResponse balanceResponse = new BalanceResponse("username", 100.0, CurrencyType.INR);
        when(userService.getBalanceByUserId(1L)).thenReturn(balanceResponse);

        mockMvc.perform(get("/users/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(100.0)));
    }

    @Test
    void testBalanceUserNotFound() throws Exception {
        when(userService.getBalance()).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/balance"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    void testBalanceByUserIdUserNotFound() throws Exception {
        when(userService.getBalanceByUserId(1L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/1/balance"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }
}
