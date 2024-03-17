package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegisterDTO registerDTO;
    private User user;

    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerDTO = new UserRegisterDTO(NAME, EMAIL, PASSWORD);
        user = new User(UUID.randomUUID(), NAME, EMAIL, PASSWORD);
    }

    @Test
    @DisplayName("Given userRegisterDTO, when register(), then calls save() of userRepository")
    void registerTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

        service.register(registerDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Given e-mail already in use, when register(), then throws FieldAlreadyInUseException")
    void registerTestCase2() {
        String errorMessage = "Error in the field 'email', the value '" + EMAIL + "' is already in use!";
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        try{
            service.register(registerDTO);
        } catch (Exception ex) {
            assertEquals(FieldAlreadyInUseException.class, ex.getClass());
            assertEquals(errorMessage, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Given invalid password, when register(), then throws InvalidPasswordException")
    void registerTestCase3() {
        String errorMessage = "The password must contain at least one uppercase letter," +
                " one lowercase letter, one number, and one special character.";

        List<String> invalidPasswords = List.of("van#001", "VAN#001", "Van001", "Van#");

        invalidPasswords.forEach(invalidPassword -> {
            registerDTO = new UserRegisterDTO(NAME, EMAIL, invalidPassword);
            when(userRepository.findByEmail(anyString())).thenReturn(null);

            try{
                service.register(registerDTO);
            } catch (Exception ex) {
                assertEquals(InvalidPasswordException.class, ex.getClass());
                assertEquals(errorMessage, ex.getMessage());
            }
        });
    }
}