package com.fundatech.shareway.paymentsettlement.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Se cobró el monto final de una reserva.
 */
public record PaymentCaptured(Long paymentId, Long bookingId, BigDecimal amount, String currency,
                              Instant occurredAt) implements DomainEvent {
}
