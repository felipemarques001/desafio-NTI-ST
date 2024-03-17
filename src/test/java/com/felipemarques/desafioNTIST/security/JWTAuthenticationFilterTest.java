package com.felipemarques.desafioNTIST.security;

import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import com.felipemarques.desafioNTIST.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JWTAuthenticationFilterTest {

    @InjectMocks
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    Authentication authentication;

    private User user;
    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";
    private final String JWT_TOKEN = "test_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(UUID.randomUUID(), NAME, EMAIL, PASSWORD);
        authentication = new UsernamePasswordAuthenticationToken(user,
                user.getPassword(),
                user.getAuthorities());
    }

    @Test
    @DisplayName("Given JWT token, when doFilter(), then authenticate user")
    void doFilterTest() throws ServletException, IOException {

        Cookie[] cookies = new Cookie[] { new Cookie("JWT_TOKEN", JWT_TOKEN) };

        when(request.getCookies()).thenReturn(cookies);
        when(tokenService.validateTokenAndGetEmail(any(String.class))).thenReturn(EMAIL);
        when(userRepository.findByEmail(any(String.class))).thenReturn(user);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(authentication.getPrincipal(), SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(authentication.getCredentials(), SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(authentication.getAuthorities(), SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Not given JWT token, when doFilter(), then not authenticate user")
    void doFilterTestCase2() throws ServletException, IOException {
        Cookie[] cookies = new Cookie[0];
        when(request.getCookies()).thenReturn(cookies);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(tokenService, times(0)).validateTokenAndGetEmail(JWT_TOKEN);
        verify(userRepository, times(0)).findByEmail(EMAIL);
        assertNotEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Given JWT token cookie, when getToken(), then return JWT token")
    void getTokenTest() {
        Cookie[] cookies = new Cookie[] { new Cookie("JWT_TOKEN", JWT_TOKEN) };
        when(request.getCookies()).thenReturn(cookies);

        String token = jwtAuthenticationFilter.getToken(request);

        assertEquals(JWT_TOKEN, token);
    }

    @Test
    @DisplayName("Not given JWT token cookie, when getToken(), then return null")
    void getTokenTestCase2() {
        Cookie[] cookies = new Cookie[0];
        when(request.getCookies()).thenReturn(cookies);

        String token = jwtAuthenticationFilter.getToken(request);

        assertNull(token);
    }
}