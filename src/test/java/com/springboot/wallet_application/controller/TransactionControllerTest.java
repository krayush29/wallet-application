package com.springboot.wallet_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TransactionController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDepositAmount100withResponseOk() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(100.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", 200.0);

        when(walletService.deposit(any(TransactionRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transactionRequest);

        MvcResult mvcResult = mockMvc.perform(put("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        TransactionResponse responseTransaction = objectMapper.readValue(response, TransactionResponse.class);

        assertEquals(transactionResponse.getUsername(), responseTransaction.getUsername());
    }

    @Test
    void testWithdrawAmount50withResponseOk() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", 150.0);

        when(walletService.withdraw(any(TransactionRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transactionRequest);

        MvcResult mvcResult = mockMvc.perform(put("/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        TransactionResponse responseTransaction = objectMapper.readValue(response, TransactionResponse.class);

        assertEquals(transactionResponse.getUsername(), responseTransaction.getUsername());
    }

    @Test
    void testTransferAmount30withResponseOk() throws Exception {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("recipient", 30.0);
        TransactionResponse transactionResponse = new TransactionResponse("user", 120.0);

        when(walletService.transfer(any(TransferMoneyRequest.class))).thenReturn(transactionResponse);

        String requestBody = objectMapper.writeValueAsString(transferMoneyRequest);

        MvcResult mvcResult = mockMvc.perform(put("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        TransactionResponse responseTransaction = objectMapper.readValue(response, TransactionResponse.class);

        assertEquals(transactionResponse.getUsername(), responseTransaction.getUsername());
    }
}