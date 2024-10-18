package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.UserRegisterRequest;
import com.springboot.wallet_application.dto.response.UserResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.DuplicateUsernameException;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.exception.WalletNotFoundException;
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

    public User registerUser(UserRegisterRequest userRequest) {
        if (getUserByUsername(userRequest.getUsername()) != null) {
            throw new DuplicateUsernameException("Username already exists: " + userRequest.getUsername());
        }

        User user = new User(userRequest.getUsername(), userRequest.getPassword(), userRequest.getCurrencyType());
        return userRepository.save(user);
    }

    public UserResponse getUserDetail() {
        Wallet wallet = walletRepository.findByUser(currentUser())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + currentUsername()));
        return new UserResponse(currentUsername(), wallet);
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