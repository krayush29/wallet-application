package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.TransferMoneyRequest;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.enums.TransactionType;
import com.springboot.wallet_application.repository.TransactionRepository;
import com.springboot.wallet_application.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private User user;

    private Wallet walletINR;
    private Wallet walletUSD;
    private User userINR;
    private User userUSD;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testUser");

        wallet = new Wallet(CurrencyType.INR);
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.deposit(1000.0);

        userINR = new User();
        userINR.setUsername("userINR");
        walletINR = new Wallet(CurrencyType.INR);
        walletINR.setId(1L);
        walletINR.setUser(userINR);
        walletINR.deposit(8500.0); // Assuming 1 USD = 85 INR

        userUSD = new User();
        userUSD.setUsername("userUSD");
        walletUSD = new Wallet(CurrencyType.USD);
        walletUSD.setId(2L);
        walletUSD.setUser(userUSD);
    }

    @Test
    void testDepositAmount500ToWallet() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(500.0);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.existsByIdAndUser(1L, user)).thenReturn(true);
        when(userService.currentUser()).thenReturn(user);

        TransactionResponse response = walletService.deposit(1L, transactionRequest);

        assertEquals(1500.0, wallet.getBalance());
        assertEquals(TransactionType.DEPOSIT, response.getTransactionType());
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testWithdrawAmount500FromWallet() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(500.0);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.existsByIdAndUser(1L, user)).thenReturn(true);
        when(userService.currentUser()).thenReturn(user);

        TransactionResponse response = walletService.withdraw(1L, transactionRequest);

        assertEquals(500.0, wallet.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, response.getTransactionType());
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testTransferAmount500ToRecipient() throws Exception {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("recipient", 500.0);
        User recipient = new User();
        recipient.setUsername("recipient");
        Wallet recipientWallet = new Wallet(CurrencyType.INR);
        recipientWallet.setUser(recipient);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.existsByIdAndUser(1L, user)).thenReturn(true);
        when(userService.currentUser()).thenReturn(user);
        when(userService.getUserByUsername("recipient")).thenReturn(recipient);
        when(walletRepository.findByUser(recipient)).thenReturn(Optional.of(recipientWallet));

        TransactionResponse response = walletService.transfer(1L, transferMoneyRequest);

        assertEquals(500.0, wallet.getBalance());
        assertEquals(500.0, recipientWallet.getBalance());
        assertEquals(TransactionType.TRANSFER, response.getTransactionType());
        verify(walletRepository, times(1)).save(wallet);
        verify(walletRepository, times(1)).save(recipientWallet);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testWithdrawInsufficientBalance() {
        TransactionRequest transactionRequest = new TransactionRequest(1500.0);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.existsByIdAndUser(1L, user)).thenReturn(true);
        when(userService.currentUser()).thenReturn(user);

        assertThrows(Exception.class, () -> walletService.withdraw(1L, transactionRequest));
    }

    @Test
    void testTransferInsufficientBalance() {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("recipient", 1500.0);
        User recipient = new User();
        recipient.setUsername("recipient");
        Wallet recipientWallet = new Wallet(CurrencyType.INR);
        recipientWallet.setUser(recipient);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.existsByIdAndUser(1L, user)).thenReturn(true);
        when(userService.currentUser()).thenReturn(user);
        when(userService.getUserByUsername("recipient")).thenReturn(recipient);
        when(walletRepository.findByUser(recipient)).thenReturn(Optional.of(recipientWallet));

        assertThrows(Exception.class, () -> walletService.transfer(1L, transferMoneyRequest));
    }

    @Test
    void testTransferAmountFromINRtoUSD() throws Exception {
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("userUSD", 8500.0);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletINR));
        when(walletRepository.existsByIdAndUser(1L, userINR)).thenReturn(true);
        when(userService.currentUser()).thenReturn(userINR);
        when(userService.getUserByUsername("userUSD")).thenReturn(userUSD);
        when(walletRepository.findByUser(userUSD)).thenReturn(Optional.of(walletUSD));

        TransactionResponse response = walletService.transfer(1L, transferMoneyRequest);

        assertEquals(0.0, walletINR.getBalance());
        assertEquals(100.0, walletUSD.getBalance());
        assertEquals(TransactionType.TRANSFER, response.getTransactionType());
        verify(walletRepository, times(1)).save(walletINR);
        verify(walletRepository, times(1)).save(walletUSD);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void testTransferAmountFromUSDtoINR() throws Exception {
        walletINR.setBalance(0.0);
        walletUSD.deposit(100.0); // Assuming 1 USD = 85 INR
        TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest("userINR", 100.0);

        when(walletRepository.findById(2L)).thenReturn(Optional.of(walletUSD));
        when(walletRepository.existsByIdAndUser(2L, userUSD)).thenReturn(true);
        when(userService.currentUser()).thenReturn(userUSD);
        when(userService.getUserByUsername("userINR")).thenReturn(userINR);
        when(walletRepository.findByUser(userINR)).thenReturn(Optional.of(walletINR));

        TransactionResponse response = walletService.transfer(2L, transferMoneyRequest);

        assertEquals(0.0, walletUSD.getBalance());
        assertEquals(8500.0, walletINR.getBalance());
        assertEquals(TransactionType.TRANSFER, response.getTransactionType());
        verify(walletRepository, times(1)).save(walletUSD);
        verify(walletRepository, times(1)).save(walletINR);
        verify(transactionRepository, times(1)).save(any());
    }
}