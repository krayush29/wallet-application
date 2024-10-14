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
    public void testUserValidationUsernameAndPasswordBlank() {
        User user = new User();
        user.setUsername("");
        user.setPassword("");

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
    public void testUserValidationPasswordAndUsernameGreaterThan5Char() {
        User user = new User();
        user.setUsername("tony");
        user.setPassword("1234");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());
    }

    @Test
    public void testUserTwoUsersCannotBeSame() {
        User user1 = new User();

        User user2 = new User();

        assertNotEquals(user2, user1);
    }
}