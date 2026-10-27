package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.Money;

/**
 * Puerto hacia la pasarela de pagos externa.
 */
public interface PaymentGateway {

    /**
     * Solicita retener el monto en la tarjeta tokenizada.
     *
     * @throws com.fundatech.shareway.paymentsettlement.shared.exception.GatewayUnavailableException
     *         si la pasarela no responde
     */
    GatewayAuthorization authorize(String cardToken, Money amount);

    /**
     * Cobra sobre una autorización previa un monto menor o igual al retenido.
     */
    void capture(String providerRef, Money amount);

    /**
     * Libera el monto retenido de una autorización que no se cobrará.
     */
    void voidAuthorization(String providerRef);

    /**
     * Resultado de una autorización: aprobada con referencia del proveedor o rechazada.
     */
    record GatewayAuthorization(boolean approved, String providerRef) {
    }
}
