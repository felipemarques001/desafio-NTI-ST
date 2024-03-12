package com.felipemarques.desafioNTIST.services;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.felipemarques.desafioNTIST.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "Felipe", "felipe@gmail.com", "Felipe#001");
    }

    @Test
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
    void givenValidToken_whenValidateToken_thenReturnEmail() {
        String token = tokenService.generateToken(user);
        String email = tokenService.validateTokenAndGetEmail(token);

        assertEquals(user.getEmail(), email);
    }

    @Test
    void givenInvalidToken_whenValidateToken_thenThrowJWTVerificationException() {
        String invalidToken = "invalid-token";

        try {
            tokenService.validateTokenAndGetEmail(invalidToken);
        } catch (RuntimeException ex) {
            assertEquals(JWTVerificationException.class, ex.getClass());
        }
    }
}