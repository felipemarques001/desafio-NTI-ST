package com.felipemarques.desafioNTIST.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.felipemarques.desafioNTIST.models.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private final String SECRET = "my-secret";
    private final String ISSUER = "NTI-ST";

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new JWTCreationException("Error while generating JWT token", ex);
        }
    }

    public String validateTokenAndGetEmail(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Invalid token");
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.ofHours(-3));
    }
}
