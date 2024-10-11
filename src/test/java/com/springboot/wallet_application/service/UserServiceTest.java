package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.TransactionRequest;
import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.dto.response.TransactionResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
        User user = new User("tonyStark", "password123");
        UserRequestBody userRequest = new UserRequestBody("tonyStark", "password123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(userRequest);

        assertNotNull(registeredUser);
        assertEquals("tonyStark", registeredUser.getUsername());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDepositAmount50ToGetTotal150() throws Exception {
        User user = new User("tonyStark", "password123");
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);
        user.setWallet(wallet);

        TransactionRequest transactionRequest = new TransactionRequest("password123", 50.0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        TransactionResponse transactionResponse = userService.deposit(1L, transactionRequest);

        assertEquals(150.0, transactionResponse.getAmount());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testWithdrawAmount50outOf100() throws Exception {
        User user = new User("tonyStark", "password123");
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);
        user.setWallet(wallet);

        TransactionRequest transactionRequest = new TransactionRequest("password123", 50.0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        TransactionResponse transactionResponse = userService.withdraw(1L, transactionRequest);

        assertEquals(50.0, transactionResponse.getAmount());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testExceptionWithdrawInsufficientBalance() {
        User user = new User("tonyStark", "password123");
        Wallet wallet = new Wallet();
        wallet.setBalance(30.0);
        user.setWallet(wallet);

        TransactionRequest transactionRequest = new TransactionRequest("password123", 50.0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(Exception.class, () -> userService.withdraw(1L, transactionRequest));
    }

    @Test
    void testExceptionForDepositAmountForUnknownUser() {
        TransactionRequest transactionRequest = new TransactionRequest();

        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.deposit(1L, transactionRequest));
    }
}