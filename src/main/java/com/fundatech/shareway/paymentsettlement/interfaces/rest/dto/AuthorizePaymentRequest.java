package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Solicitud de autorización enviada por Booking &amp; Pricing con el monto ya calculado.
 * {@code paymentMethodId} es opcional: si no llega se usa el método de pago activo más reciente del pasajero.
 */
public record AuthorizePaymentRequest(
        @NotNull UUID bookingId,
        @NotNull UUID passengerId,
        @Schema(description = "Optional. When omitted, the passenger's most recently registered active payment "
                + "method is used (422 if the passenger has none).", nullable = true)
        UUID paymentMethodId,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotBlank @Pattern(regexp = "PEN", message = "only PEN is supported") String currency) {
}
