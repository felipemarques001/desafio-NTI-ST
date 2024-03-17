package com.felipemarques.desafioNTIST.security;

import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.services.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    public UserAuthenticationSuccessHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        User userLogged = (User) authentication.getPrincipal();

        Cookie cookie = new Cookie("JWT_TOKEN", tokenService.generateToken(userLogged));
        cookie.setMaxAge(15 * 60);
        response.addCookie(cookie);

        response.sendRedirect("/tasks");
    }
}
