package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de la tabla driver_settlements.
 */
public interface JpaDriverSettlementRepository extends JpaRepository<DriverSettlement, Long> {

    Optional<DriverSettlement> findByTripId(Long tripId);
}
