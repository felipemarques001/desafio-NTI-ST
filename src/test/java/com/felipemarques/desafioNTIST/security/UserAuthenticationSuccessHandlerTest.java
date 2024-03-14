package com.felipemarques.desafioNTIST.security;

import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.services.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAuthenticationSuccessHandlerTest {

    @InjectMocks
    private UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private User user;

    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";

    private final String JWT_TOKEN = "test_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(UUID.randomUUID(), NAME, EMAIL, PASSWORD);
    }

    @Test
    void whenUserLogged_whenOnAuthenticationSuccess_thenSetCookieAndRedirect() throws IOException, ServletException {

        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);

        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response, times(1)).addCookie(argThat(cookie ->
            cookie.getName().equals("JWT_TOKEN") && cookie.getValue().equals(JWT_TOKEN)
        ));
        verify(response, times(1)).sendRedirect("/home");
    }
}