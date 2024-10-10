package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.Transaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.UserException;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.exception.WalletException;
import com.springboot.wallet_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists for: " + user.getUsername());
        }

        return userRepository.save(user);
    }

    public Wallet deposit(Transaction transaction) {
        User user = userRepository.findByUsername(transaction.getUsername());
        if (user == null) throw new UserNotFoundException("User not found for username: " + transaction.getUsername());

        try {
            user.validateUser(transaction.getPassword());
            user.depositAmount(transaction.getAmount());
            userRepository.save(user);
        } catch (UserException e) {
            throw new UserException(e.getMessage());
        } catch (WalletException e) {
            throw new WalletException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return user.getWallet();
    }

    public Wallet withdraw(Transaction transaction) {
        User user = userRepository.findByUsername(transaction.getUsername());
        if (user == null) throw new UserNotFoundException("User not found for username: " + transaction.getUsername());

        try {
            user.validateUser(transaction.getPassword());
            user.withdrawAmount(transaction.getAmount());
            userRepository.save(user);
        } catch (UserException e) {
            throw new UserException(e.getMessage());
        } catch (WalletException e) {
            throw new WalletException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return user.getWallet();
    }
}