package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import com.fundatech.shareway.paymentsettlement.domain.model.CardBrand;
import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import java.util.UUID;

/**
 * Vista pública de una tarjeta; omite el token de la pasarela.
 */
public record PaymentMethodResponse(UUID id, CardBrand brand, String last4, int expMonth, int expYear) {

    public static PaymentMethodResponse from(PaymentMethod method) {
        return new PaymentMethodResponse(method.getId(), method.getBrand(), method.getLast4(),
                method.getExpMonth(), method.getExpYear());
    }
}
