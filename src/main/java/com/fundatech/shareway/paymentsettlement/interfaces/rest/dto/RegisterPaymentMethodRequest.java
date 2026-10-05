package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import com.fundatech.shareway.paymentsettlement.domain.model.CardBrand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar una tarjeta tokenizada; nunca incluye el número completo.
 */
public record RegisterPaymentMethodRequest(
        @NotBlank @Size(max = 100) String cardToken,
        @NotNull CardBrand brand,
        @NotNull @Pattern(regexp = "\\d{4}", message = "must be exactly 4 digits") String last4,
        @NotNull @Min(1) @Max(12) Integer expMonth,
        @NotNull @Min(2000) @Max(2100) Integer expYear) {
}
