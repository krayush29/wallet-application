package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.UnauthorizedWalletException;
import com.springboot.wallet_application.exception.WalletNotFoundException;
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
    public TransactionResponse deposit(long walletId, TransactionRequest transactionRequest) throws Exception {
        Wallet wallet = getCurrentUserWallet(walletId);
        TransactionResponse transactionResponse;

        try {
            wallet.deposit(transactionRequest.getAmount());
            walletRepository.save(wallet);

            // Create and Save transaction
            Transaction transaction = new Transaction(userService.currentUser(), TransactionType.DEPOSIT, transactionRequest.getAmount(), wallet.getCurrencyType());
            transactionRepository.save(transaction);
            transactionResponse = new TransactionResponse(transaction, wallet);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return transactionResponse;
    }

    @Transactional
    public TransactionResponse withdraw(long walletId, TransactionRequest transactionRequest) throws Exception {
        Wallet wallet = getCurrentUserWallet(walletId);
        TransactionResponse transactionResponse;

        try {
            wallet.withdraw(transactionRequest.getAmount());
            walletRepository.save(wallet);

            // Create and Save transaction
            Transaction transaction = new Transaction(userService.currentUser(), TransactionType.WITHDRAWAL, transactionRequest.getAmount(), wallet.getCurrencyType());
            transactionRepository.save(transaction);
            transactionResponse = new TransactionResponse(transaction, wallet);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return transactionResponse;
    }

    @Transactional
    public TransactionResponse transfer(long walletId, TransferMoneyRequest transferMoneyRequest) throws Exception {
        Wallet fromWallet = getCurrentUserWallet(walletId);
        User recipient = userService.getUserByUsername(transferMoneyRequest.getRecipientUsername());
        Wallet toWallet = getUserWallet(recipient);

        TransactionResponse transactionResponse;

        try {
            fromWallet.transfer(toWallet, transferMoneyRequest.getTransferAmount());
            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            // Create and Save transaction
            Transaction transaction = new Transaction(userService.currentUser(), recipient, TransactionType.TRANSFER, transferMoneyRequest.getTransferAmount(), toWallet.getCurrencyType());
            transactionRepository.save(transaction);
            transactionResponse = new TransactionResponse(transaction, fromWallet);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return transactionResponse;
    }

    private Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user).orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userService.currentUser().getUsername()));
    }

    public Wallet getCurrentUserWallet(long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found for id: " + walletId));

        if (!walletRepository.existsByIdAndUser(wallet.getId(), userService.currentUser())) {
            throw new UnauthorizedWalletException("Unauthorized wallet for user: " + userService.currentUsername());
        }

        return wallet;
    }
}
