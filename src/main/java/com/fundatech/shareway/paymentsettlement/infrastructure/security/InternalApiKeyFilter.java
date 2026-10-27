package com.fundatech.shareway.paymentsettlement.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Protege {@code /api/v1/internal/**} con el header {@code X-Internal-Api-Key}, comparado en tiempo constante.
 */
public class InternalApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Internal-Api-Key";
    public static final String INTERNAL_ROLE = "INTERNAL_SERVICE";

    private final byte[] expectedKey;
    private final SecurityErrorWriter errorWriter;

    public InternalApiKeyFilter(String expectedKey, SecurityErrorWriter errorWriter) {
        this.expectedKey = expectedKey.getBytes(StandardCharsets.UTF_8);
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String provided = request.getHeader(HEADER);
        if (provided == null || !MessageDigest.isEqual(expectedKey, provided.getBytes(StandardCharsets.UTF_8))) {
            errorWriter.write(request, response, HttpStatus.UNAUTHORIZED, "Missing or invalid internal API key");
            return;
        }
        var authentication = new UsernamePasswordAuthenticationToken("internal-service", null,
                List.of(new SimpleGrantedAuthority("ROLE_" + INTERNAL_ROLE)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(request.getContextPath() + "/api/v1/internal/");
    }
}
