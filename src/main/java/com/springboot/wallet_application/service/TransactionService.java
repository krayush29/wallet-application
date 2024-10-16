package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    public List<TransactionResponse> getAllTransactions() {
        User user = userService.currentUser();
        List<Transaction> transactions = transactionRepository.findAllByFromUser(user);

        return getTransactionResponse(transactions);
    }

    public List<TransactionResponse> getTransactionsByTypes(List<TransactionType> transactionTypes) {
        User user = userService.currentUser();
        List<Transaction> transactions = transactionRepository.findAllByFromUser(user).stream()
                .filter(transaction -> transactionTypes.contains(transaction.getType()))
                .toList();

        return getTransactionResponse(transactions);
    }

    private List<TransactionResponse> getTransactionResponse(List<Transaction> transactions) {
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionResponses.add(new TransactionResponse(transaction));
        }

        return transactionResponses;
    }
}
