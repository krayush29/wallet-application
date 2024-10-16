package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.WalletException;
import com.springboot.wallet_application.repository.TransactionRepository;
import com.springboot.wallet_application.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(TransactionRequest transactionRequest) throws Exception {
        Wallet wallet = getCurrentUserWallet();
        TransactionResponse transactionResponse;

        try {
            wallet.deposit(transactionRequest.getAmount());
            walletRepository.save(wallet);

            // Create and Save transaction
            String message = "Deposited amount " + transactionRequest.getAmount() + " to " + userService.currentUsername();
            Transaction transaction = new Transaction(userService.currentUser(), TransactionType.DEPOSIT, transactionRequest.getAmount(), message);
            transactionRepository.save(transaction);
            transactionResponse = new TransactionResponse(transaction);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return transactionResponse;
    }

    @Transactional
    public TransactionResponse withdraw(TransactionRequest transactionRequest) throws Exception {
        Wallet wallet = getCurrentUserWallet();
        TransactionResponse transactionResponse;

        try {
            wallet.withdraw(transactionRequest.getAmount());
            walletRepository.save(wallet);

            // Create and Save transaction
            String message = "Deducted amount " + transactionRequest.getAmount() + " from " + userService.currentUsername();
            Transaction transaction = new Transaction(userService.currentUser(), TransactionType.WITHDRAWAL, transactionRequest.getAmount(), message);
            transactionRepository.save(transaction);
            transactionResponse = new TransactionResponse(transaction);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return transactionResponse;
    }

    @Transactional
    public TransactionResponse transfer(TransferMoneyRequest transferMoneyRequest) throws Exception {
        Wallet fromWallet = getCurrentUserWallet();
        User recipient = userService.getUserByUsername(transferMoneyRequest.getRecipientUsername());
        Wallet toWallet = getUserWallet(recipient);

        TransactionResponse transactionResponse;

        try {
            fromWallet.transfer(toWallet, transferMoneyRequest.getTransferAmount());
            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            // Create and Save transaction
            String message = "Transferred amount " + transferMoneyRequest.getTransferAmount() + " to " + recipient + " from " + userService.currentUsername();
            Transaction transaction = new Transaction(userService.currentUser(), recipient, TransactionType.TRANSFER, transferMoneyRequest.getTransferAmount(), message);
            transactionRepository.save(transaction);
            transactionResponse = new TransactionResponse(transaction);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return transactionResponse;
    }

    private Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user).orElseThrow(() -> new WalletException("Wallet not found for user: " + userService.currentUser().getUsername()));
    }

    private Wallet getCurrentUserWallet() {
        return walletRepository.findByUser(userService.currentUser()).orElseThrow(() -> new WalletException("Wallet not found for user: " + userService.currentUsername()));
    }
}
