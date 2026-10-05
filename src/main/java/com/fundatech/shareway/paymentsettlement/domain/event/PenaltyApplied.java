package com.fundatech.shareway.paymentsettlement.domain.event;

import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Se cobró una penalidad por cancelación tardía o no presentación.
 */
public record PenaltyApplied(Long penaltyId, Long paymentId, Long bookingId, Long passengerId, BigDecimal amount,
                             String currency, CancellationReason reason, Instant occurredAt) implements DomainEvent {
}
