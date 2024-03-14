package com.felipemarques.desafioNTIST.security;

import com.felipemarques.desafioNTIST.services.AuthenticationService;
import com.felipemarques.desafioNTIST.services.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final TokenService tokenService;

    public SecurityConfigurations(JWTAuthenticationFilter jwtAuthenticationFilter,
                                  TokenService tokenService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll())
                .logout(LogoutConfigurer::permitAll)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new UserAuthenticationSuccessHandler(tokenService);
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder amb =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        amb.userDetailsService(authenticationService())
                .passwordEncoder(passwordEncoder());

        return amb.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}