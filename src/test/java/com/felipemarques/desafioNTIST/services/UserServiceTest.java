package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

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

    UserRegisterDTO dto;
    User user;

    @BeforeEach
    void setUp() {
        dto = new UserRegisterDTO("vanessa", "vanessa@gmail.com", "123");
        user = new User(UUID.randomUUID(), "vanessa", "vanessa@gmail.com", "123");
    }

    @Test
    void giverUserRegisterDTO_whenRegister_thenVerify() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        doNothing().when(userRepository).save(any(User.class));

        service.register(dto);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUserRegisterDTO_whenEmailInUse_thenThrowResourceAlreadyInUseException() {
        String errorMessage = "Error in the field 'email', the value '" + dto.email() + "' is already in use!";
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        try{
            service.register(dto);
        } catch (Exception ex) {
            assertEquals(FieldAlreadyInUseException.class, ex.getClass());
            assertEquals(errorMessage, ex.getMessage());
        }
    }

    @Test
    void givenUsername_whenLoadUserByUsername_thenReturnUserDetails() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDetails response = service.loadUserByUsername("vanessa@gmail.com");

        assertEquals("vanessa@gmail.com", response.getUsername());
        assertEquals("123", response.getPassword());
    }
}