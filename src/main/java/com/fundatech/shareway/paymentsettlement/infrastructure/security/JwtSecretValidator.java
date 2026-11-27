package com.fundatech.shareway.paymentsettlement.infrastructure.security;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Impide arrancar el servicio en producción con el secreto JWT vacío, con el valor de ejemplo o demasiado corto.
 * Este servicio confía en cualquier token firmado con esa clave, así que el secreto de desarrollo compartido y
 * conocido nunca debe usarse en producción. La verificación se relaja con los perfiles dev, test y local, donde
 * el valor de ejemplo de {@code application-dev.properties} es aceptable.
 */
@Component
public class JwtSecretValidator {

    /** Valor de ejemplo de desarrollo; está copiado en application-dev.properties y deben coincidir. */
    static final String EXAMPLE_SECRET = "dev-only-shareway-driver-management-secret-change-me";
    private static final Set<String> RELAXED_PROFILES = Set.of("dev", "test", "local");
    private static final int MIN_SECRET_LENGTH = 32;

    private final String secret;
    private final Environment environment;

    public JwtSecretValidator(@Value("${app.jwt.secret:}") String secret, Environment environment) {
        this.secret = secret;
        this.environment = environment;
    }

    @PostConstruct
    void validate() {
        if (isRelaxedProfile()) {
            return;
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET must be set in production. Configure the JWT_SECRET environment variable "
                            + "with a strong random value of at least " + MIN_SECRET_LENGTH + " characters.");
        }
        if (EXAMPLE_SECRET.equals(secret)) {
            throw new IllegalStateException(
                    "JWT_SECRET is still the development example value. Set a strong, unique JWT_SECRET in production.");
        }
        if (secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "JWT_SECRET is too short; use at least " + MIN_SECRET_LENGTH + " characters.");
        }
    }

    private boolean isRelaxedProfile() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(RELAXED_PROFILES::contains);
    }
}
