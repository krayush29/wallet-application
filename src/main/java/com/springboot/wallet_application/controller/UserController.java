package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.dto.request.UserRegisterRequest;
import com.springboot.wallet_application.dto.response.UserResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details. By Default currency will be INR")
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserRegisterRequest UserRegisterRequest) {
        User userResponse = userService.registerUser(UserRegisterRequest);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Get current user details", description = "Fetches the details of the currently authenticated user.")
    @GetMapping()
    public ResponseEntity<Object> getUser() {
        UserResponse userResponse = userService.getUserDetail();
        return ResponseEntity.ok(userResponse);
    }
}