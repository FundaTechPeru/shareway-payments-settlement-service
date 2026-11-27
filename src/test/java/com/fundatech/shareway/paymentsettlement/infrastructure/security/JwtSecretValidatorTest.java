package com.fundatech.shareway.paymentsettlement.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

/**
 * Pruebas unitarias de la validación del secreto JWT según el perfil activo.
 */
class JwtSecretValidatorTest {

    private static final String STRONG_SECRET = "a-strong-production-secret-with-enough-length-123";

    @Test
    void failsWhenSecretIsMissingInProduction() {
        assertThatThrownBy(() -> validate("", "prod"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET must be set");
    }

    @Test
    void failsWhenSecretIsNullInProduction() {
        assertThatThrownBy(() -> validate(null, "prod"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET must be set");
    }

    @Test
    void failsWhenSecretIsMissingAndNoProfileIsActive() {
        assertThatThrownBy(() -> validate(""))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET must be set");
    }

    @Test
    void failsWhenSecretIsStillTheDevelopmentExampleInProduction() {
        assertThatThrownBy(() -> validate(JwtSecretValidator.EXAMPLE_SECRET, "prod"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("example value");
    }

    @Test
    void failsWhenSecretIsTooShortInProduction() {
        assertThatThrownBy(() -> validate("short-secret", "prod"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("too short");
    }

    @Test
    void acceptsAStrongSecretInProduction() {
        assertThatCode(() -> validate(STRONG_SECRET, "prod")).doesNotThrowAnyException();
    }

    @Test
    void acceptsTheExampleSecretUnderTheDevProfile() {
        assertThatCode(() -> validate(JwtSecretValidator.EXAMPLE_SECRET, "dev")).doesNotThrowAnyException();
    }

    @Test
    void acceptsAnythingUnderTheTestProfile() {
        assertThatCode(() -> validate("short", "test")).doesNotThrowAnyException();
    }

    @Test
    void acceptsTheExampleSecretUnderTheLocalProfile() {
        assertThatCode(() -> validate(JwtSecretValidator.EXAMPLE_SECRET, "local")).doesNotThrowAnyException();
    }

    @Test
    void enforcesTheRulesWhenARelaxedProfileIsNotActive() {
        assertThatThrownBy(() -> validate(JwtSecretValidator.EXAMPLE_SECRET, "staging", "docker"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("example value");
    }

    private static void validate(String secret, String... activeProfiles) {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(activeProfiles);
        new JwtSecretValidator(secret, environment).validate();
    }
}
