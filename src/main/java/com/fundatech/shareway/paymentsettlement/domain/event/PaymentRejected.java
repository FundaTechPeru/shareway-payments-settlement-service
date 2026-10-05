package com.fundatech.shareway.paymentsettlement.domain.event;

import java.time.Instant;

/**
 * La pasarela rechazó la autorización del pago de una reserva.
 */
public record PaymentRejected(Long paymentId, Long bookingId, Long passengerId, Instant occurredAt)
        implements DomainEvent {
}
