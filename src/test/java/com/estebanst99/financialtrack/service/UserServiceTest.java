package com.estebanst99.financialtrack.service;

import com.estebanst99.financialtrack.entity.User;
import com.estebanst99.financialtrack.exception.UserServiceException;
import com.estebanst99.financialtrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    //Inyecta automáticamente los mocks necesarios (en este caso, userRepository) en los campos de UserService.
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        //Inicializa los mocks y los objetos anotados con @InjectMocks antes de cada prueba.
        //Sin esta inicialización, las anotaciones no tendrían efecto y los mocks no estarían disponibles.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByEmail_UserExists() {

        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void testFindByEmail_UserNotExists() {

        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail(email);

        assertFalse(result.isPresent());
    }

    @Test
    void testSave_UserAlreadyExists() {

        String email = "duplicate@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.save(user));
        assertEquals("El email ya está registrado.", exception.getMessage());
    }

    @Test
    void testSave_NewUser() throws UserServiceException {

        User user = new User();
        user.setEmail("newuser@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(user);
    }
}
