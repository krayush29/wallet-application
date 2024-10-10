package com.springboot.wallet_application.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletTest {

    @Test
    public void testWalletBalance() {
        Wallet wallet = new Wallet();

        wallet.setBalance(100.0);

        assertEquals(100.0, wallet.getBalance());
    }

    @Test
    public void testWalletUserAssociation() {
        Wallet wallet = new Wallet();

        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password");

        wallet.setUser(user);
        user.setWallet(wallet);

        assertEquals(user, wallet.getUser());
        assertEquals(wallet, user.getWallet());
    }
}