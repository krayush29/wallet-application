package com.springboot.wallet_application.exception;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
