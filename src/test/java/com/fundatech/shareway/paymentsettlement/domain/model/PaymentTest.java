package com.fundatech.shareway.paymentsettlement.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fundatech.shareway.paymentsettlement.shared.exception.ConflictException;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de las transiciones de estado del agregado {@link Payment}.
 */
class PaymentTest {

    private static final Money AMOUNT = Money.of(new BigDecimal("30.00"), "PEN");

    @Test
    void authorizesPendingPayment() {
        Payment payment = Payment.create(100L, 1L, 10L, AMOUNT);

        payment.markAuthorized("sim_ref", Instant.now());

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
        assertThat(payment.isAuthorizedOrCaptured()).isTrue();
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> Payment.create(100L, 1L, 10L, Money.zero("PEN")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cannotAuthorizeTwice() {
        Payment payment = Payment.create(100L, 1L, 10L, AMOUNT);
        payment.markAuthorized("sim_ref", Instant.now());

        assertThatThrownBy(() -> payment.markAuthorized("sim_other", Instant.now()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void rejectedPaymentCanBeRetriedBySamePassenger() {
        Payment payment = Payment.create(100L, 1L, 10L, AMOUNT);
        payment.markRejected();

        payment.retry(1L, 11L, AMOUNT);
        payment.markAuthorized("sim_ref", Instant.now());

        assertThat(payment.getPaymentMethodId()).isEqualTo(11L);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void authorizedPaymentCannotBeRetried() {
        Payment payment = Payment.create(100L, 1L, 10L, AMOUNT);
        payment.markAuthorized("sim_ref", Instant.now());

        assertThatThrownBy(() -> payment.retry(1L, 11L, AMOUNT)).isInstanceOf(ConflictException.class);
    }
}
