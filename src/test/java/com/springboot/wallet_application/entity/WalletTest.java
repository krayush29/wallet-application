package com.springboot.wallet_application.entity;

import com.springboot.wallet_application.exception.WalletException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {

    @Test
    public void testByDefaultWalletBalanceShouldBeZero() {
        Wallet wallet = new Wallet();

        assertEquals(0.0, wallet.getBalance());
    }

    @Test
    public void testWalletBalanceWith100Amount() {
        Wallet wallet = new Wallet();

        wallet.setBalance(100.0);

        assertEquals(100.0, wallet.getBalance());
    }

    @Test
    public void testWalletUserOneOnOneAssociation() {
        Wallet wallet = new Wallet();

        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password");

        wallet.setUser(user);
        user.setWallet(wallet);

        assertEquals(user, wallet.getUser());
        assertEquals(wallet, user.getWallet());
    }

    @Test
    public void testDepositAmount50() {
        Wallet wallet = new Wallet();
        wallet.deposit(50.0);
        assertEquals(50.0, wallet.getBalance());
    }

    @Test
    public void testWithdrawAmount50outOf100() {
        Wallet wallet = new Wallet();
        wallet.deposit(100.0);
        wallet.withdraw(50.0);
        assertEquals(50.0, wallet.getBalance());
    }

    @Test
    public void testWithdrawInsufficientBalance() {
        Wallet wallet = new Wallet();
        wallet.deposit(50.0);
        assertThrows(WalletException.class, () -> wallet.withdraw(100.0));
    }
}