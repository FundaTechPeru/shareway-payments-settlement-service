package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Evento TripCompleted de Trip Execution con el monto final de cada reserva del viaje.
 */
public record TripCompletedRequest(
        @NotNull @Positive Long tripId,
        @NotNull @Positive Long driverId,
        @NotNull Instant completedAt,
        @NotEmpty List<@Valid @NotNull BookingCharge> bookings) {

    /**
     * Monto final de una reserva del viaje.
     */
    public record BookingCharge(
            @NotNull @Positive Long bookingId,
            @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction = 2)
            BigDecimal finalAmount) {
    }
}
