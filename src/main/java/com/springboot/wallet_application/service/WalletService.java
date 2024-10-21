package com.springboot.wallet_application.service;

import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.WalletNotFoundException;
import com.springboot.wallet_application.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    public Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user).orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + userService.currentUser().getUsername()));
    }

    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
