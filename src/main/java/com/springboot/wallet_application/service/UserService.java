package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(UserRequestBody userRequest) {
        User user = new User(userRequest.getUsername(), userRequest.getPassword());
        return userRepository.save(user);
    }

    public TransactionResponse deposit(Long userId, TransactionRequest transactionRequest) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new UserNotFoundException("User not found for username: " + userId);

        try {
            user.validateUser(transactionRequest.getPassword());
            user.depositAmount(transactionRequest.getAmount());
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new TransactionResponse(user.getUsername(), user.getWallet().getBalance());
    }

    public TransactionResponse withdraw(Long userId, TransactionRequest transactionRequest) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new UserNotFoundException("User not found for username: " + userId);

        try {
            user.validateUser(transactionRequest.getPassword());
            user.withdrawAmount(transactionRequest.getAmount());
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new TransactionResponse(user.getUsername(), user.getWallet().getBalance());
    }
}