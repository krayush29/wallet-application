package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.WalletException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletService walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUsername");
        wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(100.0);
    }

    @Test
    void testDepositAmount50To100() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        when(userService.currentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        TransactionResponse response = walletService.deposit(transactionRequest);

        assertEquals(150.0, response.getAmount());
    }

    @Test
    void testWithdrawAmount50From100() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        when(userService.currentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        TransactionResponse response = walletService.withdraw(transactionRequest);

        assertEquals(50.0, response.getAmount());
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

        assertEquals(70.0, response.getAmount());
    }

    @Test
    void testDepositWalletNotFound() {
        TransactionRequest transactionRequest = new TransactionRequest(50.0);
        when(userService.currentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> walletService.deposit(transactionRequest));
    }
}