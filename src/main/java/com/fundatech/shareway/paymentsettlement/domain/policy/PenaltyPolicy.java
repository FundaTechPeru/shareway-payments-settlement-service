package com.fundatech.shareway.paymentsettlement.domain.policy;

import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import com.fundatech.shareway.paymentsettlement.domain.model.Money;
import java.time.Instant;
import java.util.Optional;

/**
 * Estrategia que decide si una cancelación genera penalidad y por cuánto.
 */
public interface PenaltyPolicy {

    /**
     * Devuelve el monto a cobrar como penalidad, o vacío si la autorización debe anularse sin costo.
     */
    Optional<Money> penaltyFor(CancellationReason reason, Instant cancelledAt, Instant scheduledDepartureAt,
                               Money authorizedAmount);
}
