package com.springboot.wallet_application.entity;

import com.springboot.wallet_application.enums.CurrencyType;
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

    @Test
    public void testConversionINRtoINR() {
        Wallet wallet = new Wallet(CurrencyType.INR);
        double amountInINR = 100.0;
        double convertedAmount = wallet.convert(CurrencyType.INR, CurrencyType.INR, amountInINR);

        assertEquals(amountInINR, convertedAmount);
    }

    @Test
    public void testConversionINRtoUSD() {
        Wallet wallet = new Wallet(CurrencyType.INR);
        double amountInINR = 100.0;
        double convertedAmount = wallet.convert(CurrencyType.INR, CurrencyType.USD, amountInINR);

        // Assuming 1 USD = 85 INR  for this test case
        double expectedAmountInUSD = 1.18;
        assertEquals(expectedAmountInUSD, convertedAmount, 0.01);
    }

    @Test
    public void testConversionUSDtoINR() {
        Wallet wallet = new Wallet(CurrencyType.USD);
        double amountInUSD = 100.0;
        double convertedAmount = wallet.convert(CurrencyType.USD, CurrencyType.INR, amountInUSD);

        // Assuming 1 USD = 85 INR for this test case
        double expectedAmountInINR = 8500.0;
        assertEquals(expectedAmountInINR, convertedAmount, 0.01);
    }

    @Test
    public void testConversionUSDtoUSD() {
        Wallet wallet = new Wallet(CurrencyType.USD);
        double amountInUSD = 100.0;
        double convertedAmount = wallet.convert(CurrencyType.USD, CurrencyType.USD, amountInUSD);

        assertEquals(amountInUSD, convertedAmount);
    }
}