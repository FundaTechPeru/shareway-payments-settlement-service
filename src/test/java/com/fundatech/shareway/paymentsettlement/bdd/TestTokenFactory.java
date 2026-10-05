package com.fundatech.shareway.paymentsettlement.bdd;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Firma tokens de prueba con el secreto del perfil {@code test}, imitando los que emite Driver Management.
 */
@Component
public class TestTokenFactory {

    private final SecretKey key;

    public TestTokenFactory(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Token vigente con los claims {@code sub}, {@code userId} y {@code role}.
     */
    public String token(Long userId, String role) {
        return base(userId, Duration.ofMinutes(15)).claim("role", role).compact();
    }

    /**
     * Token con los claims correctos pero ya expirado.
     */
    public String expiredToken(Long userId, String role) {
        return base(userId, Duration.ofMinutes(-5)).claim("role", role).compact();
    }

    /**
     * Token vigente al que le falta el claim {@code role}.
     */
    public String tokenWithoutRole(Long userId) {
        return base(userId, Duration.ofMinutes(15)).compact();
    }

    private JwtBuilder base(Long userId, Duration validity) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("user" + userId + "@shareway.test")
                .claim("userId", userId)
                .issuedAt(Date.from(now.minus(Duration.ofMinutes(10))))
                .expiration(Date.from(now.plus(validity)))
                .signWith(key);
    }
}
