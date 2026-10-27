package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import com.fundatech.shareway.paymentsettlement.domain.model.SettlementStatus;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de las liquidaciones a conductores.
 */
public interface DriverSettlementRepository {

    DriverSettlement save(DriverSettlement settlement);

    Optional<DriverSettlement> findById(Long id);

    Optional<DriverSettlement> findByTripId(Long tripId);

    Optional<DriverSettlement> findByIdAndDriverId(Long id, Long driverId);

    List<DriverSettlement> findByDriverId(Long driverId);

    List<DriverSettlement> findAll(Optional<SettlementStatus> status);
}
