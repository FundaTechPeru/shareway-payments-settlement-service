package com.fundatech.shareway.paymentsettlement.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de la validación de tokens y extracción de claims.
 */
class JwtServiceTest {

    private static final String SECRET = "unit-test-shareway-jwt-secret-with-32-bytes";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private final JwtService jwtService = new JwtService(SECRET);

    @Test
    void extractsUserIdAndRoleFromValidToken() {
        String token = Jwts.builder().subject("ana@upc.edu.pe").claim("userId", 7L).claim("role", "DRIVER")
                .expiration(Date.from(Instant.now().plusSeconds(600))).signWith(KEY).compact();

        AuthenticatedUser user = jwtService.parse(token);

        assertThat(user.userId()).isEqualTo(7L);
        assertThat(user.role()).isEqualTo(UserRole.DRIVER);
        assertThat(user.email()).isEqualTo("ana@upc.edu.pe");
    }

    @Test
    void rejectsExpiredToken() {
        String token = Jwts.builder().subject("ana@upc.edu.pe").claim("userId", 7L).claim("role", "DRIVER")
                .expiration(Date.from(Instant.now().minusSeconds(60))).signWith(KEY).compact();

        assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsTokenWithoutRoleClaim() {
        String token = Jwts.builder().subject("ana@upc.edu.pe").claim("userId", 7L)
                .expiration(Date.from(Instant.now().plusSeconds(600))).signWith(KEY).compact();

        assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        SecretKey otherKey = Keys.hmacShaKeyFor("another-secret-that-is-at-least-32-bytes".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("ana@upc.edu.pe").claim("userId", 7L).claim("role", "DRIVER")
                .signWith(otherKey).compact();

        assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(JwtException.class);
    }
}
