package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserLoginDTO;
import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(UUID.randomUUID(), NAME, EMAIL, PASSWORD);
    }

    @Test
    void givenUsername_whenLoadUserByUsername_thenReturnUserDetails() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDetails response = authenticationService.loadUserByUsername(EMAIL);

        assertEquals(EMAIL, response.getUsername());
        assertEquals(PASSWORD, response.getPassword());
    }
}