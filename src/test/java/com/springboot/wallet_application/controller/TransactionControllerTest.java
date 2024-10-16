package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}