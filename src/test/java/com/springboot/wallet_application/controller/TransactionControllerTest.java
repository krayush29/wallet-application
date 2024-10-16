package com.springboot.wallet_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.service.TransactionService;
import com.springboot.wallet_application.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TransactionController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchAllTransactionWithValidURI() throws Exception {

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/transactions?types=DEPOSIT"))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/transactions?types=DEPOSIT, WITHDRAWAL,TRANSFER"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testBadRequestResponseFetchTransactionWithInvalidURI() throws Exception {
        mockMvc.perform(get("/transactions?types=INVALID"))
                .andExpect(status().isBadRequest())
                .andReturn();

        mockMvc.perform(get("/transactions?types="))
                .andExpect(status().isBadRequest())
                .andReturn();


        mockMvc.perform(get("/transactions?DEPOSIT"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testDepositAmount100withResponseOk() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(100.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", TransactionType.DEPOSIT, 100.0, 200.0, "message");

        when(walletService.deposit(any(TransactionRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transactionRequest);

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testWithdrawAmount50withResponseOk() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", TransactionType.WITHDRAWAL, 50.0, 150.0, "message");

        when(walletService.withdraw(any(TransactionRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transactionRequest);

        mockMvc.perform(post("/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testTransferAmount30withResponseOk() throws Exception {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("recipient", 30.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", TransactionType.TRANSFER, 30.0, 120.0, "message");

        when(walletService.transfer(any(TransferMoneyRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transferMoneyRequest);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();
    }
}