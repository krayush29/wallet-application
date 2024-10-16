package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.dto.response.TransactionHistoryResponse;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
