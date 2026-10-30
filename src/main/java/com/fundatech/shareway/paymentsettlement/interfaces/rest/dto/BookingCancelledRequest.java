package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Evento BookingCancelled de Booking &amp; Pricing.
 */
public record BookingCancelledRequest(
        @NotNull UUID bookingId,
        @NotNull CancellationReason reason,
        @NotNull Instant cancelledAt,
        @NotNull Instant scheduledDepartureAt) {
}
