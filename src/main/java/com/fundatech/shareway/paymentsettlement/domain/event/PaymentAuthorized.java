package com.fundatech.shareway.paymentsettlement.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * La pasarela aprobó la autorización del pago de una reserva.
 */
public record PaymentAuthorized(Long paymentId, Long bookingId, Long passengerId, BigDecimal amount,
                                String currency, Instant occurredAt) implements DomainEvent {
}
