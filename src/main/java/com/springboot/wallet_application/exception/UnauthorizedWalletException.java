package com.springboot.wallet_application.exception;

public class UnauthorizedWalletException extends WalletException {
    public UnauthorizedWalletException(String message) {
        super(message);
    }
}
