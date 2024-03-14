package com.felipemarques.desafioNTIST.security;

import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import com.felipemarques.desafioNTIST.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    @Mock
    private SecurityContextHolder securityContextHolder;

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
    void givenToken_whenDoFilter_thenAuthenticateUser() throws ServletException, IOException {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        when(request.getHeader(any(String.class))).thenReturn("Bearer " + JWT_TOKEN);
        when(tokenService.validateTokenAndGetEmail(any(String.class))).thenReturn(EMAIL);
        when(userRepository.findByEmail(any(String.class))).thenReturn(user);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(authentication.getPrincipal(), SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(authentication.getCredentials(), SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertEquals(authentication.getAuthorities(), SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void notGivenToken_whenDoFilter_thenNotAuthenticateUserWithToken() throws ServletException, IOException {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(request.getHeader(any(String.class))).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(tokenService, times(0)).validateTokenAndGetEmail(JWT_TOKEN);
        verify(userRepository, times(0)).findByEmail(EMAIL);
        assertNotEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void givenToken_whenGetToken_thenReturnToken()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String token = "test_token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Method getTokenMethod = JWTAuthenticationFilter.class
                .getDeclaredMethod("getToken", HttpServletRequest.class);
        getTokenMethod.setAccessible(true);
        String result = (String) getTokenMethod.invoke(jwtAuthenticationFilter, request);

        assertEquals(token, result);
    }

    @Test
    void notGivenToken_whenGetToken_thenReturnNull()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getTokenMethod = JWTAuthenticationFilter.class
                .getDeclaredMethod("getToken", HttpServletRequest.class);

        getTokenMethod.setAccessible(true);

        when(request.getHeader("Authorization")).thenReturn(null);

        String result = (String) getTokenMethod.invoke(jwtAuthenticationFilter, request);

        assertNull(result);
    }
}