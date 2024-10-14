package com.springboot.wallet_application.service;

import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}