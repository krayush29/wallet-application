package com.springboot.wallet_application.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.dto.request.UserRegisterRequest;
import com.springboot.wallet_application.dto.response.UserResponse;
import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.DuplicateUsernameException;
import com.springboot.wallet_application.exception.UserNotFoundException;
import com.springboot.wallet_application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserValidUser() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("tonyStark", "password");
        String requestBody = objectMapper.writeValueAsString(userRegisterRequest);

        User user = new User("tonyStark", "password");
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        User responseUser = objectMapper.readValue(response, User.class);

        assertEquals(userRegisterRequest.getUsername(), responseUser.getUsername());
    }

    @Test
    void testBadRequestForInvalidUserEmptyUsername() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("", "password");
        String requestBody = objectMapper.writeValueAsString(userRegisterRequest);

        User user = new User("", "password");
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("{\"username\":\"Username should have at least 5 characters\"}"))).andReturn();
    }

    @Test
    void testBadRequestForInvalidUserNullUsername() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(null, "password");
        String requestBody = objectMapper.writeValueAsString(userRegisterRequest);

        User user = new User(null, "password");
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("{\"username\":\"Username cannot be null\"}"))).andReturn();
    }

    @Test
    void testBadRequestForInvalidUserWithEmptyPassword() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("username", "");
        String requestBody = objectMapper.writeValueAsString(userRegisterRequest);

        User user = new User("username", "");
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("\"password\":\"Password should have at least 5 characters\""))).andReturn();
    }

    @Test
    void testBadRequestForInvalidUserWithNullPassword() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("username", null);
        String requestBody = objectMapper.writeValueAsString(userRegisterRequest);

        User user = new User("username", null);
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("{\"password\":\"Username cannot be null\"}"))).andReturn();
    }

    @Test
    void testUserBalanceIs100() throws Exception {
        UserResponse userResponse = new UserResponse("username", 100.0, CurrencyType.INR);
        when(userService.getUserDetail()).thenReturn(userResponse);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(100.0)));
    }

    @Test
    void testBalanceUserNotFound() throws Exception {
        when(userService.getUserDetail()).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }


    @Test
    public void whenCurrencyTypeIsPassedShouldTakeThatCurrencyType() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("username", "password", "USD");
        User user = new User("username", "password");
        user.getWallet().setCurrencyType(CurrencyType.USD);

        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet.currencyType").value("USD"))
                .andReturn();
    }

    @Test
    public void whenCurrencyTypeIsNotPassedShouldDefaultToINR() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("username", "password");
        User user = new User("username", "password");
        user.getWallet().setCurrencyType(CurrencyType.INR);

        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(user);


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wallet.currencyType").value("INR"))
                .andReturn();
    }

    @Test
    public void whenIncorrectCurrencyTypeIsPassedShouldReturnValidationError() throws Exception {
        String requestJson = "{\"username\":\"testuser\",\"password\":\"password\",\"currencyType\":\"INVALID\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"error\":\"JSON parse error: Invalid currency type: INVALID. Please provide a valid currency type.[USD, INR]\"}"))).andReturn();
    }

    @Test
    public void testBadRequestForUsernameAlreadyExists() throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest("existingUser", "password");
        String requestBody = objectMapper.writeValueAsString(userRegisterRequest);

        when(userService.registerUser(any(UserRegisterRequest.class)))
                .thenThrow(new DuplicateUsernameException("Username already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Username already exists")));
    }
}
