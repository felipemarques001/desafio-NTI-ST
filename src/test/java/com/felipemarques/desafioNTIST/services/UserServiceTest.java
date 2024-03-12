package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserLoginDTO;
import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    private User user;

    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerDTO = new UserRegisterDTO(NAME, EMAIL, PASSWORD);
        loginDTO = new UserLoginDTO(EMAIL, PASSWORD);
        user = new User(UUID.randomUUID(), NAME, EMAIL, PASSWORD);
    }

    @Test
    void givenUserRegisterDTO_whenRegister_thenVerify() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

        service.register(registerDTO);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void givenUserRegisterDTO_whenEmailInUse_thenThrowResourceAlreadyInUseException() {
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
    void givenInvalidPasswords_whenRegister_thenThrowInvalidPasswordException() {
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

    @Test
    void givenUserAuthenticated_whenLogin_thenReturnToken() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String token = "mocked_token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn(token);

        String generatedToken = service.login(loginDTO);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals(token, generatedToken);
    }
}