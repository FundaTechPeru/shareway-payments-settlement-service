package com.fundatech.shareway.paymentsettlement.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias del registro y desactivación de métodos de pago.
 */
class PaymentMethodTest {

    private static final UUID PASSENGER = UUID.randomUUID();
    private static final YearMonth NOW = YearMonth.of(2026, 10);

    @Test
    void registersActiveCardExpiringThisMonth() {
        PaymentMethod method = PaymentMethod.register(PASSENGER, "tok_visa", CardBrand.VISA, "4242", 10, 2026, NOW);

        assertThat(method.isUsableBy(PASSENGER)).isTrue();
        assertThat(method.isUsableBy(UUID.randomUUID())).isFalse();
    }

    @Test
    void rejectsExpiredCard() {
        assertThatThrownBy(() -> PaymentMethod.register(PASSENGER, "tok_visa", CardBrand.VISA, "4242", 9, 2026, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deactivatedCardIsNotUsable() {
        PaymentMethod method = PaymentMethod.register(PASSENGER, "tok_visa", CardBrand.VISA, "4242", 12, 2030, NOW);

        method.deactivate();

        assertThat(method.isUsableBy(PASSENGER)).isFalse();
    }
}
