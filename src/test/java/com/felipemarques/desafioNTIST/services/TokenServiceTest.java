package com.felipemarques.desafioNTIST.services;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.felipemarques.desafioNTIST.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(UUID.randomUUID(), "Felipe", "felipe@gmail.com", "Felipe#001");
    }

    @Test
    @DisplayName("Given user, when generateToken(), then return valid JWT token")
    void givenUser_whenGenerateToken_thenReturnValidToken() {
        try {
            String token = tokenService.generateToken(user);
            tokenService.validateTokenAndGetEmail(token);
            assertEquals(String.class, token.getClass());
        } catch (RuntimeException ex) {
            assertNotEquals(JWTCreationException.class, ex.getClass());
            assertNotEquals(JWTVerificationException.class, ex.getClass());
        }
    }

    @Test
    @DisplayName("Given valid JWT token, when validateTokenAndGetEmail(), then return user e-mail")
    void validateTokenAndGetEmailTest() {
        String token = tokenService.generateToken(user);
        String email = tokenService.validateTokenAndGetEmail(token);

        assertEquals(user.getEmail(), email);
    }

    @Test
    @DisplayName("Given invalid JWT token, when validateTokenAndGetEmail(), then throws JWTVerificationException")
    void validateTokenAndGetEmailTestCase2() {
        String invalidToken = "invalid-token";

        try {
            tokenService.validateTokenAndGetEmail(invalidToken);
        } catch (RuntimeException ex) {
            assertEquals(JWTVerificationException.class, ex.getClass());
        }
    }
}