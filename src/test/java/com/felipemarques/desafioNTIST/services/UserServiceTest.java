package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegisterDTO dto;
    private User user;

    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        dto = new UserRegisterDTO(NAME, EMAIL, PASSWORD);
        user = new User(UUID.randomUUID(), NAME, EMAIL, PASSWORD);
    }

    @Test
    void givenUserRegisterDTO_whenRegister_thenVerify() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        doNothing().when(userRepository).save(any(User.class));

        service.register(dto);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUserRegisterDTO_whenEmailInUse_thenThrowResourceAlreadyInUseException() {
        String errorMessage = "Error in the field 'email', the value '" + EMAIL + "' is already in use!";
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        try{
            service.register(dto);
        } catch (Exception ex) {
            assertEquals(FieldAlreadyInUseException.class, ex.getClass());
            assertEquals(errorMessage, ex.getMessage());
        }
    }

    @Test
    void givenInvalidPasswords_whenRegister_thenThrowInvalidPasswordException() {
        String errorMessage = "The password must contain at least one uppercase letter," +
                " one lowercase letter, one number, and one special character.";

        List<String> invalidPasswords = List.of("van#001", "VAN#001", "Van001", "Van#");

        invalidPasswords.forEach(invalidPassword -> {
            dto = new UserRegisterDTO(NAME, EMAIL, invalidPassword);
            when(userRepository.findByEmail(anyString())).thenReturn(null);

            try{
                service.register(dto);
            } catch (Exception ex) {
                assertEquals(InvalidPasswordException.class, ex.getClass());
                assertEquals(errorMessage, ex.getMessage());
            }
        });
    }

    @Test
    void givenUsername_whenLoadUserByUsername_thenReturnUserDetails() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDetails response = service.loadUserByUsername(EMAIL);

        assertEquals(EMAIL, response.getUsername());
        assertEquals(PASSWORD, response.getPassword());
    }
}