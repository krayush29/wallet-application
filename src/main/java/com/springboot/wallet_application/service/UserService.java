package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(UserRequestBody userRequest) {
        User user = new User(userRequest.getUsername(), userRequest.getPassword());
        return userRepository.save(user);
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