package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.Transaction;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.exception.WalletException;
import com.springboot.wallet_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserWithValidUser() {
        User user = new User();
        user.setUsername("tonyStark");

        when(userRepository.findByUsername("tonyStark")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDepositAmount50ToGetTotal150() {
        User user = new User();
        user.setUsername("tonyStark");
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);
        user.setWallet(wallet);

        Transaction transaction = new Transaction();
        transaction.setUsername("tonyStark");
        transaction.setAmount(50.0);

        when(userRepository.findByUsername("tonyStark")).thenReturn(user);

        Wallet updatedWallet = userService.deposit(transaction);

        assertEquals(150.0, updatedWallet.getBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testWithdrawAmount50outOf100() {
        User user = new User();
        user.setUsername("tonyStark");
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);
        user.setWallet(wallet);

        Transaction transaction = new Transaction();
        transaction.setUsername("tonyStark");
        transaction.setAmount(50.0);

        when(userRepository.findByUsername("tonyStark")).thenReturn(user);

        Wallet updatedWallet = userService.withdraw(transaction);

        assertEquals(50.0, updatedWallet.getBalance());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testExceptionWithdrawInsufficientBalance() {
        User user = new User();
        user.setUsername("tonyStark");
        Wallet wallet = new Wallet();
        wallet.setBalance(30.0);
        user.setWallet(wallet);

        Transaction transaction = new Transaction();
        transaction.setUsername("tonyStark");
        transaction.setAmount(50.0);

        when(userRepository.findByUsername("tonyStark")).thenReturn(user);

        assertThrows(WalletException.class, () -> userService.withdraw(transaction));
    }

    @Test
    void testExceptionForDepositAmountForUnknownUser() {
        Transaction transaction = new Transaction();
        transaction.setUsername("unknownUser");

        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.deposit(transaction));
    }
}