package com.fundatech.shareway.paymentsettlement.domain.policy;

import com.fundatech.shareway.paymentsettlement.domain.model.Money;

/**
 * Estrategia para calcular la comisión de la plataforma sobre el monto bruto de un viaje.
 */
public interface CommissionPolicy {

    Money commissionFor(Money gross);
}
