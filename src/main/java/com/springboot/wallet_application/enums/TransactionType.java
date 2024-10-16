package com.springboot.wallet_application.enums;

import java.util.Optional;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER;

    public static Optional<TransactionType> fromString(String type) {
        try {
            return Optional.of(TransactionType.valueOf(type.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
