package com.springboot.wallet_application.exception;

public class WalletException extends RuntimeException {
    public WalletException(String message) {
        super(message);
    }
}
