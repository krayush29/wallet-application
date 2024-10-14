package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.WalletException;
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

    @Transactional
    public TransactionResponse deposit(TransactionRequest transactionRequest) throws Exception {
        Wallet wallet = getCurrentUserWallet();

        try {
            wallet.deposit(transactionRequest.getAmount());
            walletRepository.save(wallet);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new TransactionResponse(userService.currentUsername(), wallet.getBalance());
    }

    @Transactional
    public TransactionResponse withdraw(TransactionRequest transactionRequest) throws Exception {
        Wallet wallet = getCurrentUserWallet();

        try {
            wallet.withdraw(transactionRequest.getAmount());
            walletRepository.save(wallet);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new TransactionResponse(userService.currentUsername(), wallet.getBalance());
    }

    @Transactional
    public TransactionResponse transfer(TransferMoneyRequest transferMoneyRequest) throws Exception {
        Wallet fromWallet = getCurrentUserWallet();
        User recipient = userService.getUserByUsername(transferMoneyRequest.getRecipientUsername());
        Wallet toWallet = getUserWallet(recipient);

        try {
            fromWallet.transfer(toWallet, transferMoneyRequest.getTransferAmount());
            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new TransactionResponse(userService.currentUsername(), fromWallet.getBalance());
    }

    private Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user).orElseThrow(() -> new WalletException("Wallet not found for user: " + userService.currentUser().getUsername()));
    }

    private Wallet getCurrentUserWallet() {
        return walletRepository.findByUser(userService.currentUser()).orElseThrow(() -> new WalletException("Wallet not found for user: " + userService.currentUsername()));
    }
}
