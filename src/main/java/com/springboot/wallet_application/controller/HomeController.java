package com.springboot.wallet_application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HomeController {

    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Wallet Application, Please register to use the services");
    }
}
