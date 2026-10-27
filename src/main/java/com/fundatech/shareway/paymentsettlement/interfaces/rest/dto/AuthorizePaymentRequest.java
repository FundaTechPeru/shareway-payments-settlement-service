package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Solicitud de autorización enviada por Booking &amp; Pricing con el monto ya calculado.
 */
public record AuthorizePaymentRequest(
        @NotNull @Positive Long bookingId,
        @NotNull @Positive Long passengerId,
        @NotNull @Positive Long paymentMethodId,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotBlank @Pattern(regexp = "PEN", message = "only PEN is supported") String currency) {
}
