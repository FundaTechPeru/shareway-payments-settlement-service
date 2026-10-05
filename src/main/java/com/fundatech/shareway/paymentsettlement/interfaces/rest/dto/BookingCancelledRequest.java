package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

/**
 * Evento BookingCancelled de Booking &amp; Pricing.
 */
public record BookingCancelledRequest(
        @NotNull @Positive Long bookingId,
        @NotNull CancellationReason reason,
        @NotNull Instant cancelledAt,
        @NotNull Instant scheduledDepartureAt) {
}
