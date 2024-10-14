package com.springboot.wallet_application.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.dto.request.UserRequestBody;
import com.springboot.wallet_application.entity.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        UserRequestBody userRequestBody = new UserRequestBody("tonyStark", "password");
        String requestBody = objectMapper.writeValueAsString(userRequestBody);

        User user = new User("tonyStark", "password");
        when(userService.registerUser(any(UserRequestBody.class))).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        User responseUser = objectMapper.readValue(response, User.class);

        assertEquals(userRequestBody.getUsername(), responseUser.getUsername());
    }

    @Test
    void testBadRequestForInvalidUserEmptyUsername() throws Exception {
        UserRequestBody userRequestBody = new UserRequestBody("", "password");
        String requestBody = objectMapper.writeValueAsString(userRequestBody);

        User user = new User("", "password");
        when(userService.registerUser(any(UserRequestBody.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError()).andReturn();
    }
}
