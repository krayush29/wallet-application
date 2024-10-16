package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionHistoryResponse;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.service.TransactionService;
import com.springboot.wallet_application.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping()
    public ResponseEntity<Object> getTransactions(@RequestParam(value = "types", required = false) List<String> types, HttpServletRequest request) {

        if ((request.getParameterMap().size() > 1) ||
                (request.getParameterMap().size() == 1 && (!request.getParameterMap().containsKey("types") ||
                        request.getParameter("types").isEmpty()))) {
            return ResponseEntity.badRequest().body("Invalid URI or Query parameters");
        }

        List<TransactionHistoryResponse> transactions;
        List<TransactionType> transactionTypes = new ArrayList<>();

        if (types != null) {
            for (String type : types) {
                Optional<TransactionType> transactionType = TransactionType.fromString(type);
                if (transactionType.isPresent()) {
                    transactionTypes.add(transactionType.get());
                } else {
                    return ResponseEntity.badRequest().body("Invalid transaction type: " + type);
                }
            }
        }

        transactions = transactionTypes.isEmpty() ?
                transactionService.getAllTransactions() :
                transactionService.getTransactionsByTypes(transactionTypes);

        // Fetch and return transactions based on the type
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/deposit")
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

    @PostMapping("/withdrawal")
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

    @PostMapping("/transfer")
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
