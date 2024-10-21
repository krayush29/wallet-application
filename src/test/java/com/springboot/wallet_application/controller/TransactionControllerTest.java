package com.springboot.wallet_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.response.transaction.SelfTransactionResponse;
import com.springboot.wallet_application.dto.response.transaction.TransactionResponse;
import com.springboot.wallet_application.dto.response.transaction.TransferTransactionResponse;
import com.springboot.wallet_application.entity.SelfTransaction;
import com.springboot.wallet_application.entity.TransferTransaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TransactionController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TransactionService transactionService;

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
    void testTransactionDepositAmount100ToUser() throws Exception {
        User user = new User("username", "password");
        user.getWallet().setBalance(100.0);
        SelfTransaction selfTransaction = new SelfTransaction(user, TransactionType.DEPOSIT, 100.0);
        selfTransaction.setTimestamp(LocalDateTime.now());

        TransactionResponse response = new SelfTransactionResponse(selfTransaction, user);
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionType\":\"DEPOSIT\",\"amount\":100.0,\"recipientUsername\":\"username\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"username\":\"username\", \"currentBalance\":100.0}"));
    }

    @Test
    void testTransactionWithdrawAmount50FromUser() throws Exception {
        User user = new User("username", "password");
        user.getWallet().setBalance(100.0);
        SelfTransaction selfTransaction = new SelfTransaction(user, TransactionType.DEPOSIT, 50.0);
        selfTransaction.setTimestamp(LocalDateTime.now());

        TransactionResponse response = new SelfTransactionResponse(selfTransaction, user);
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionType\":\"WITHDRAWAL\",\"amount\":50.0,\"recipientUsername\":\"username\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"username\":\"username\", \"currentBalance\":100.0}"));
    }

    @Test
    void testTransactionTransferAmount50ToRecipientUser() throws Exception {
        User sender = new User("senderUsername", "password");
        User recipient = new User("recipientUsername", "password");
        sender.getWallet().setBalance(50.0);

        // mock response
        TransferTransaction transferTransaction = new TransferTransaction(sender, recipient, TransactionType.TRANSFER, 50.0);
        transferTransaction.setTimestamp(LocalDateTime.now());
        TransactionResponse response = new TransferTransactionResponse(transferTransaction, sender, recipient);
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionType\":\"TRANSFER\",\"amount\":50.0,\"recipientUsername\":\"recipientUsername\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"currentBalance\":50.0}"));
    }

    @Test
    void testCreateTransactionInvalidAmount() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionType\":\"DEPOSIT\",\"amount\":-100.0,\"recipientUsername\":\"recipientUser\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTransactionInvalidTransactionType() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionType\":\"INVALID\",\"amount\":100.0,\"recipientUsername\":\"recipientUser\"}"))
                .andExpect(status().isBadRequest());
    }
}