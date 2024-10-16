package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.dto.response.BalanceResponse;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public ResponseEntity<Object> balance() {
        BalanceResponse balanceResponse;
        try {
            balanceResponse = userService.getBalance();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(balanceResponse);
    }

    // Just a Helper API to get balance by userId
    // Not recommended as It is not secure to expose balance of any user
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Object> balanceByUserId(@PathVariable Long userId) {
        BalanceResponse balanceResponse;
        try {
            balanceResponse = userService.getBalanceByUserId(userId);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(balanceResponse);
    }
}