package com.springboot.wallet_application.controller;

import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {


    @Autowired
    private UserService userService;

    //localhost:8080/users/register
    //localhost:8080/users/{id}/deposit
    //localhost:8080/users/{id}/withdraw

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody @Valid UserRequestBody UserRequestBody) {
        // Implement the registerUser method here

        User userResponse;
        try {
            userResponse = userService.registerUser(UserRequestBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(userResponse);
    }
}
