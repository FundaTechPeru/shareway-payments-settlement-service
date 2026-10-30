package com.fundatech.shareway.paymentsettlement.domain.event;

import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Se cobró una penalidad por cancelación tardía o no presentación.
 */
public record PenaltyApplied(UUID penaltyId, UUID paymentId, UUID bookingId, UUID passengerId, BigDecimal amount,
                             String currency, CancellationReason reason, Instant occurredAt) implements DomainEvent {
}
