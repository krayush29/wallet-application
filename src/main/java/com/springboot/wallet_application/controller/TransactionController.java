package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private WalletService walletService;

    @PutMapping("/deposit")
    public ResponseEntity<Object> deposit(@RequestBody @Valid TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse;

        try {
            transactionResponse = walletService.deposit(transactionRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(transactionResponse);
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<Object> withdrawal(@RequestBody @Valid TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse;

        try {
            transactionResponse = walletService.withdraw(transactionRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(transactionResponse);
    }

    @PutMapping("/transfer")
    public ResponseEntity<Object> transfer(@RequestBody @Valid TransferMoneyRequest transferMoneyRequest) {
        TransactionResponse transactionResponse;

        try {
            transactionResponse = walletService.transfer(transferMoneyRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(transactionResponse);
    }
}
