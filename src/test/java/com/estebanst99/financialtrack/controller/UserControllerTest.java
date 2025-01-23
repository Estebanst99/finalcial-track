package com.estebanst99.financialtrack.controller;

import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.UserServiceException;
import com.estebanst99.financialtrack.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByEmail_UserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByEmail_UserDoesNotExist() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateUser_Success() throws UserServiceException {
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.save(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testCreateUser_Failure() throws UserServiceException {
        when(userService.save(any(User.class))).thenThrow(new RuntimeException());

        User user = new User();
        user.setEmail("test@example.com");
        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}