package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.dto.response.BalanceResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.exception.WalletException;
import com.springboot.wallet_application.repository.UserRepository;
import com.springboot.wallet_application.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    public User registerUser(UserRequestBody userRequest) {
        User user = new User(userRequest.getUsername(), userRequest.getPassword());
        return userRepository.save(user);
    }

    public BalanceResponse getBalance() {
        Wallet wallet = walletRepository.findByUser(currentUser())
                .orElseThrow(() -> new WalletException("Wallet not found for user: " + currentUsername()));
        return new BalanceResponse(currentUsername(), wallet.getBalance());
    }

    public BalanceResponse getBalanceByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletException("Wallet not found for user: " + user.getUsername()));

        return new BalanceResponse(user.getUsername(), wallet.getBalance());
    }

    public User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found for username: " + username));
    }

    public String currentUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found for username: " + username))
                .getUsername();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Recipient not found for username: " + username));
    }
}