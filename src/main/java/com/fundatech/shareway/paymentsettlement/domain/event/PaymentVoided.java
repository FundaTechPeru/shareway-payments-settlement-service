package com.fundatech.shareway.paymentsettlement.domain.event;

import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import java.time.Instant;

/**
 * Se anuló la autorización de una reserva cancelada sin penalidad.
 */
public record PaymentVoided(Long paymentId, Long bookingId, CancellationReason reason, Instant occurredAt)
        implements DomainEvent {
}
