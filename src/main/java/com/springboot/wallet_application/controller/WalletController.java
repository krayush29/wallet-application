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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;


    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<Object> deposit(@PathVariable long walletId, @RequestBody @Valid TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse;

        try {
            transactionResponse = walletService.deposit(walletId, transactionRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping("/{walletId}/withdrawal")
    public ResponseEntity<Object> withdrawal(@PathVariable long walletId, @RequestBody @Valid TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse;

        try {
            transactionResponse = walletService.withdraw(walletId, transactionRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping("/{walletId}/transfer")
    public ResponseEntity<Object> transfer(@PathVariable long walletId, @RequestBody @Valid TransferMoneyRequest transferMoneyRequest) {
        TransactionResponse transactionResponse;

        try {
            transactionResponse = walletService.transfer(walletId, transferMoneyRequest);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(transactionResponse);
    }
}
