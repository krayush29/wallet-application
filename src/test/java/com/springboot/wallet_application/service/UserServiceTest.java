package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.UserRegisterRequest;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.DuplicateUsernameException;
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
import static org.mockito.ArgumentMatchers.anyString;
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
        User user = new User("username1", "password123");
        UserRegisterRequest userRequest = new UserRegisterRequest("username1", "password1");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        User registeredUser = userService.registerUser(userRequest);

        assertNotNull(registeredUser);
        assertEquals("username1", registeredUser.getUsername());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithDefaultCurrencyTypeINR() {
        User user = new User("tonyStark", "password123", CurrencyType.INR);
        UserRegisterRequest userRequest = new UserRegisterRequest("tonyStark", "password123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        User registeredUser = userService.registerUser(userRequest);

        assertNotNull(registeredUser);
        assertEquals("tonyStark", registeredUser.getUsername());
        assertEquals(CurrencyType.INR, registeredUser.getWallet().getCurrencyType());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testExceptionUsernameAlreadyExists() {
        UserRegisterRequest userRequest = new UserRegisterRequest("tonyStark", "password123");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> userService.registerUser(userRequest));
    }
}