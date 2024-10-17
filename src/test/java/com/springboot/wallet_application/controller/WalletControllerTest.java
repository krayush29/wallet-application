package com.springboot.wallet_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.service.TransactionService;
import com.springboot.wallet_application.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = WalletController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testDepositAmount100withResponseOk() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(100.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", TransactionType.DEPOSIT, 100.0, 200.0, CurrencyType.INR, "message", "time");

        when(walletService.deposit(any(Long.class), any(TransactionRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transactionRequest);

        mockMvc.perform(post("/wallets/{walletId}/deposit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testWithdrawAmount50withResponseOk() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", TransactionType.WITHDRAWAL, 50.0, 150.0, CurrencyType.INR, "message", "time");

        when(walletService.withdraw(any(Long.class), any(TransactionRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transactionRequest);

        mockMvc.perform(post("/wallets/{walletId}/withdrawal", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testTransferAmount30withResponseOk() throws Exception {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("recipient", 30.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", TransactionType.TRANSFER, 30.0, 120.0, CurrencyType.INR, "message", "time");

        when(walletService.transfer(any(Long.class), any(TransferMoneyRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transferMoneyRequest);

        mockMvc.perform(post("/wallets/{walletId}/transfer", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
    }
}