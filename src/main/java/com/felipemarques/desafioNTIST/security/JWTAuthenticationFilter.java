package com.felipemarques.desafioNTIST.security;

import com.felipemarques.desafioNTIST.repositories.UserRepository;
import com.felipemarques.desafioNTIST.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public JWTAuthenticationFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);
        if(token != null) {
            String login = tokenService.validateTokenAndGetEmail(token);
            UserDetails user = userRepository.findByEmail(login);
            Authentication authentication = UsernamePasswordAuthenticationToken
                    .authenticated(user, user.getPassword(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    public String getToken(HttpServletRequest request) {
        List<Cookie> cookies = List.of(request.getCookies());
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT_TOKEN")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
