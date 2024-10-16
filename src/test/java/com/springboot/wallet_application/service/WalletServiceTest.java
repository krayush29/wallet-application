package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.WalletException;
import com.springboot.wallet_application.repository.TransactionRepository;
import com.springboot.wallet_application.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    private User user;
    private User userINR;
    private User userUSD;

    private Wallet wallet;
    private Wallet walletINR;
    private Wallet walletUSD;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUsername");
        wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(100.0);

        userINR = new User();
        userINR.setUsername("userINR");
        walletINR = new Wallet(CurrencyType.INR);
        walletINR.setUser(userINR);
        userINR.setWallet(walletINR);

        userUSD = new User();
        userUSD.setUsername("userUSD");
        walletUSD = new Wallet(CurrencyType.USD);
        walletUSD.setUser(userUSD);
        userUSD.setWallet(walletUSD);
    }

    @Test
    void testDepositAmount50FromCurrentUser() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        when(userService.currentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        TransactionResponse response = walletService.deposit(transactionRequest);

        assertEquals(50.0, response.getTransactionAmount());
        assertEquals(150.0, response.getCurrentBalance());
    }

    @Test
    void testWithdrawAmount50FromCurrentUser() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        when(userService.currentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        TransactionResponse response = walletService.withdraw(transactionRequest);

        assertEquals(50.0, response.getTransactionAmount());
        assertEquals(50.0, response.getCurrentBalance());
    }

    @Test
    void testTransferAmount30FromCurrentUserToRecipientUser() throws Exception {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("recipient", 30.0);
        User recipient = new User();
        recipient.setUsername("recipient");
        Wallet recipientWallet = new Wallet();
        recipientWallet.setUser(recipient);
        recipientWallet.setBalance(100.0);

        when(userService.currentUser()).thenReturn(user);
        when(userService.getUserByUsername("recipient")).thenReturn(recipient);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(walletRepository.findByUser(recipient)).thenReturn(Optional.of(recipientWallet));

        TransactionResponse response = walletService.transfer(transferMoneyRequest);

        assertEquals(30.0, response.getTransactionAmount());
        assertEquals(70.0, response.getCurrentBalance());
    }

    @Test
    void testDepositWalletNotFound() {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        when(userService.currentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> walletService.deposit(transactionRequest));
    }

    @Test
    void testTransferAmountFromINRtoUSD() throws Exception {
        walletINR.deposit(8500.0); // Assuming 1 USD = 85 INR

        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("userUSD", 8500.0);

        when(userService.currentUser()).thenReturn(userINR);
        when(userService.getUserByUsername("userUSD")).thenReturn(userUSD);
        when(walletRepository.findByUser(userINR)).thenReturn(Optional.of(walletINR));
        when(walletRepository.findByUser(userUSD)).thenReturn(Optional.of(walletUSD));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        walletService.transfer(transferMoneyRequest);

        assertEquals(0.0, walletINR.getBalance());
        assertEquals(100.0, walletUSD.getBalance());
    }

    @Test
    void testTransferAmountFromUSDtoINR() throws Exception {
        walletUSD.deposit(100.0); // Assuming 1 USD = 85 INR

        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("userINR", 100.0);

        when(userService.currentUser()).thenReturn(userUSD);
        when(userService.getUserByUsername("userINR")).thenReturn(userINR);
        when(walletRepository.findByUser(userUSD)).thenReturn(Optional.of(walletUSD));
        when(walletRepository.findByUser(userINR)).thenReturn(Optional.of(walletINR));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        walletService.transfer(transferMoneyRequest);

        assertEquals(0.0, walletUSD.getBalance());
        assertEquals(8500.0, walletINR.getBalance());
    }


    @Test
    void testTransferInsufficientBalance() {
        walletINR.deposit(100.0);

        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("userUSD", 8500.0);

        when(userService.currentUser()).thenReturn(userINR);
        when(userService.getUserByUsername("userUSD")).thenReturn(userUSD);
        when(walletRepository.findByUser(userINR)).thenReturn(Optional.of(walletINR));
        when(walletRepository.findByUser(userUSD)).thenReturn(Optional.of(walletUSD));

        assertThrows(Exception.class, () -> walletService.transfer(transferMoneyRequest));
    }
}