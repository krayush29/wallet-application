package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.response.transaction.SelfTransactionResponse;
import com.springboot.wallet_application.dto.response.transaction.TransferTransactionResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.exception.InvalidTransactionException;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.repository.SelfTransactionRepository;
import com.springboot.wallet_application.repository.TransferTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {
    @Mock
    private SelfTransactionRepository selfTransactionRepository;

    @Mock
    private TransferTransactionRepository transferTransactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSelfTransactionDepositAmount100() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType("DEPOSIT");
        request.setAmount(100.0);

        User user = new User();
        Wallet wallet = new Wallet();

        when(userService.currentUser()).thenReturn(user);
        when(walletService.getUserWallet(user)).thenReturn(wallet);

        SelfTransactionResponse response = (SelfTransactionResponse) transactionService.createTransaction(request);

        assertNotNull(response);
        verify(walletService, times(1)).save(wallet);
        verify(selfTransactionRepository, times(1)).save(any());
    }

    @Test
    void testSelfTransactionWithdrawAmount50() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType("WITHDRAWAL");
        request.setAmount(50.0);

        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);

        when(userService.currentUser()).thenReturn(user);
        when(walletService.getUserWallet(user)).thenReturn(wallet);

        SelfTransactionResponse response = (SelfTransactionResponse) transactionService.createTransaction(request);

        assertNotNull(response);
        verify(walletService, times(1)).save(wallet);
        verify(selfTransactionRepository, times(1)).save(any());
    }

    @Test
    void testTransactionTransferAmount200ToRecipientUser() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.TRANSFER.toString());
        request.setAmount(200.0);
        request.setRecipientUsername("recipientUser");

        User user = new User();
        Wallet fromWallet = new Wallet();
        fromWallet.setBalance(500.0);
        user.setWallet(fromWallet);
        User recipientUser = new User();
        Wallet toWallet = new Wallet();

        when(userService.currentUser()).thenReturn(user);
        when(walletService.getUserWallet(user)).thenReturn(fromWallet);
        when(userService.getUserByUsername("recipientUser")).thenReturn(recipientUser);
        when(walletService.getUserWallet(recipientUser)).thenReturn(toWallet);

        TransferTransactionResponse response = (TransferTransactionResponse) transactionService.createTransaction(request);

        assertNotNull(response);
        verify(walletService, times(1)).save(fromWallet);
        verify(walletService, times(1)).save(toWallet);
        verify(transferTransactionRepository, times(1)).save(any());
    }

    @Test
    void testExceptionCreateTransactionWithInvalidRecipientUser() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.TRANSFER.toString());
        request.setAmount(200.0);
        request.setRecipientUsername("invalidUser");

        when(userService.currentUser()).thenReturn(new User());
        when(userService.getUserByUsername("invalidUser")).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            transactionService.createTransaction(request);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testExceptionTransactionRequestWithNullValue() {

        Exception exception = assertThrows(InvalidTransactionException.class, () -> {
            transactionService.createTransaction(new TransactionRequest());
        });

        assertEquals("Invalid transaction type: null", exception.getMessage());
    }

    @Test
    void testValidateTransaction_TransferWithNullRecipientUsername() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.TRANSFER.toString());
        request.setAmount(100.0);
        request.setRecipientUsername(null);

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.validateTransaction(request);
        });
    }

    @Test
    void testValidateTransferWithEmptyRecipientUsername() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.TRANSFER.toString());
        request.setAmount(100.0);
        request.setRecipientUsername("");

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.validateTransaction(request);
        });
    }

    @Test
    void testExceptionForUsernameLessThan5Char() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.TRANSFER.toString());
        request.setAmount(100.0);
        request.setRecipientUsername("abc");

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.validateTransaction(request);
        });
    }

    @Test
    void testExceptionForTransactionTransferToSelf() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.TRANSFER.toString());
        request.setAmount(100.0);
        request.setRecipientUsername("currentUser");

        when(userService.currentUser()).thenReturn(new User("currentUser", "password"));

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.validateTransaction(request);
        });
    }

    @Test
    void testExceptionForValidateDepositWithRecipientUsername() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.DEPOSIT.toString());
        request.setAmount(100.0);
        request.setRecipientUsername("recipientUser");

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.validateTransaction(request);
        });
    }

    @Test
    void testExceptionForWithdrawalWithRecipientUsername() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(TransactionType.WITHDRAWAL.toString());
        request.setAmount(100.0);
        request.setRecipientUsername("recipientUser");

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.validateTransaction(request);
        });
    }
}