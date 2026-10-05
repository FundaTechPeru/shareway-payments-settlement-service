package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import java.util.Optional;

/**
 * Puerto de persistencia de las liquidaciones a conductores.
 */
public interface DriverSettlementRepository {

    DriverSettlement save(DriverSettlement settlement);

    Optional<DriverSettlement> findByTripId(Long tripId);
}
