package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.response.transaction.SelfTransactionResponse;
import com.springboot.wallet_application.dto.response.transaction.TransactionResponse;
import com.springboot.wallet_application.dto.response.transaction.TransferTransactionResponse;
import com.springboot.wallet_application.dto.response.transaction.history.SelfTransactionHistoryResponse;
import com.springboot.wallet_application.dto.response.transaction.history.TransactionHistoryResponse;
import com.springboot.wallet_application.dto.response.transaction.history.TransferTransactionHistoryResponse;
import com.springboot.wallet_application.entity.SelfTransaction;
import com.springboot.wallet_application.entity.TransferTransaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.InvalidTransactionException;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.repository.SelfTransactionRepository;
import com.springboot.wallet_application.repository.TransferTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private SelfTransactionRepository selfTransactionRepository;

    @Autowired
    private TransferTransactionRepository transferTransactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    CurrencyConversionService conversionService;

    public List<TransactionHistoryResponse> getAllTransactions(List<TransactionType> transactionTypes) {
        User user = userService.currentUser();

        List<TransactionHistoryResponse> transactionResponses = new ArrayList<>();

        // Fetch all Self Transactions
        List<SelfTransaction> selfTransactions = selfTransactionRepository.findAllByUser(user);
        for (SelfTransaction selfTransaction : selfTransactions) {
            transactionResponses.add(new SelfTransactionHistoryResponse(selfTransaction, user));
        }

        // Fetch all Transfer Transactions user has sent
        List<TransferTransaction> transactionsSent = transferTransactionRepository.findAllByFromUser(user);
        for (TransferTransaction transferTransaction : transactionsSent) {
            transactionResponses.add(new TransferTransactionHistoryResponse(transferTransaction, user, transferTransaction.getToUser()));
        }

        // Fetch all Transfer Transactions user has received
        List<TransferTransaction> transactionsReceived = transferTransactionRepository.findAllByToUser(user);
        for (TransferTransaction transferTransaction : transactionsReceived) {
            transactionResponses.add(new TransferTransactionHistoryResponse(transferTransaction, user, transferTransaction.getToUser()));
        }

        // Filter transaction responses based on the type
        if (transactionTypes != null && !transactionTypes.isEmpty()) {
            List<TransactionHistoryResponse> filteredTransactionResponses = new ArrayList<>();
            for (TransactionHistoryResponse transactionResponse : transactionResponses) {
                if (transactionTypes.contains(transactionResponse.getTransactionType())) {
                    filteredTransactionResponses.add(transactionResponse);
                }
            }
            return filteredTransactionResponses;
        }

        return transactionResponses;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        try {
            validateTransaction(transactionRequest);

            if (transactionRequest.getTransactionType() == TransactionType.DEPOSIT) {
                return depositAmount(transactionRequest);
            } else if (transactionRequest.getTransactionType() == TransactionType.WITHDRAWAL) {
                return withdrawalAmount(transactionRequest);
            } else if (transactionRequest.getTransactionType() == TransactionType.TRANSFER) {
                return transferAmount(transactionRequest);
            } else {
                throw new InvalidTransactionException("Invalid transaction type: " + transactionRequest.getTransactionType());
            }
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (InvalidTransactionException e) {
            throw new InvalidTransactionException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public SelfTransactionResponse depositAmount(TransactionRequest request) {
        User currentUser = userService.currentUser();
        Wallet wallet = walletService.getUserWallet(currentUser);
        SelfTransactionResponse selfTransactionResponse;

        try {
            wallet.deposit(request.getAmount());
            walletService.save(wallet);

            // Create and Save transaction
            SelfTransaction selfTransaction = new SelfTransaction(currentUser, TransactionType.DEPOSIT, request.getAmount());
            selfTransactionRepository.save(selfTransaction);
            selfTransactionResponse = new SelfTransactionResponse(selfTransaction, currentUser);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return selfTransactionResponse;
    }

    @Transactional
    public SelfTransactionResponse withdrawalAmount(TransactionRequest request) {
        User currentUser = userService.currentUser();
        Wallet wallet = walletService.getUserWallet(currentUser);
        SelfTransactionResponse selfTransactionResponse;

        try {
            wallet.withdraw(request.getAmount());
            walletService.save(wallet);

            // Create and Save transaction
            SelfTransaction selfTransaction = new SelfTransaction(currentUser, TransactionType.WITHDRAWAL, request.getAmount());
            selfTransactionRepository.save(selfTransaction);
            selfTransactionResponse = new SelfTransactionResponse(selfTransaction, currentUser);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return selfTransactionResponse;
    }

    @Transactional
    public TransferTransactionResponse transferAmount(TransactionRequest request) {
        User currentUser = userService.currentUser();
        Wallet fromWallet = currentUser.getWallet();

        User recipientUser = userService.getUserByUsername(request.getRecipientUsername());
        Wallet toWallet = walletService.getUserWallet(recipientUser);

        TransferTransactionResponse transferTransactionResponse;

        try {
            double convertedAmount = conversionService.convert(fromWallet.getCurrencyType(), toWallet.getCurrencyType(), request.getAmount());
            fromWallet.withdraw(request.getAmount());
            toWallet.deposit(convertedAmount);

            walletService.save(fromWallet);
            walletService.save(toWallet);

            // Create and Save transaction
            TransferTransaction transferTransaction = new TransferTransaction(currentUser, recipientUser, TransactionType.TRANSFER, request.getAmount());
            transferTransactionRepository.save(transferTransaction);
            transferTransactionResponse = new TransferTransactionResponse(transferTransaction, currentUser, recipientUser);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return transferTransactionResponse;
    }

    public void validateTransaction(TransactionRequest request) {
        if (request.getTransactionType() == TransactionType.TRANSFER) {
            if (request.getRecipientUsername() == null || request.getRecipientUsername().isEmpty())
                throw new InvalidTransactionException("Recipient username cannot be null for transfer transactions");

            if (request.getRecipientUsername().length() < 5)
                throw new InvalidTransactionException("Recipient username should have at least 5 characters");

            if (request.getRecipientUsername().equals(userService.currentUser().getUsername()))
                throw new InvalidTransactionException("Cannot transfer to self, please provide a valid recipient username");
        }

        if (request.getTransactionType() == TransactionType.DEPOSIT || request.getTransactionType() == TransactionType.WITHDRAWAL) {
            if (request.getRecipientUsername() != null)
                throw new InvalidTransactionException("Recipient username is not required for deposit and withdrawal transactions");
        }
    }
}