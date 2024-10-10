package com.springboot.wallet_application.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testUserValidationValidUser() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void testUserValidationUsernameBlank() {
        User user = new User();
        user.setUsername("");
        user.setPassword("validPassword");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    public void testUserValidationUsernameAndPasswordNull() {
        User user = new User();
        user.setUsername(null);
        user.setPassword(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());
    }

    @Test
    public void testUserValidationPasswordBlank() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }


    @Test
    public void testUserValidationPassword() {
        User user1 = new User();

        User user2 = new User();

        assertNotEquals(user2, user1);
    }
}