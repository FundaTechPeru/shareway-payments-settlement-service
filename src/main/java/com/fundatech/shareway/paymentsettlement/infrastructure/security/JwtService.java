package com.fundatech.shareway.paymentsettlement.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Valida los tokens HS256 emitidos por Driver Management. Este servicio no emite tokens.
 */
@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifica firma y expiración y devuelve el usuario descrito por los claims {@code userId} (UUID) y {@code role}.
     *
     * @throws JwtException si el token es inválido, expiró o le falta alguno de los claims requeridos
     */
    public AuthenticatedUser parse(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        String userId = claims.get("userId", String.class);
        String role = claims.get("role", String.class);
        if (userId == null || role == null) {
            throw new MalformedJwtException("Token is missing userId or role claim");
        }
        try {
            return new AuthenticatedUser(UUID.fromString(userId), claims.getSubject(), UserRole.valueOf(role));
        } catch (IllegalArgumentException ex) {
            throw new MalformedJwtException("Invalid userId or role claim");
        }
    }
}
