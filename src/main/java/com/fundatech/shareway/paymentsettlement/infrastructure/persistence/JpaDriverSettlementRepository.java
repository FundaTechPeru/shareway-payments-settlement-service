package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import com.fundatech.shareway.paymentsettlement.domain.model.SettlementStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de la tabla driver_settlements.
 */
public interface JpaDriverSettlementRepository extends JpaRepository<DriverSettlement, Long> {

    Optional<DriverSettlement> findByTripId(Long tripId);

    Optional<DriverSettlement> findByIdAndDriverId(Long id, Long driverId);

    List<DriverSettlement> findByDriverIdOrderByIdDesc(Long driverId);

    List<DriverSettlement> findByStatusOrderByIdDesc(SettlementStatus status);

    List<DriverSettlement> findAllByOrderByIdDesc();
}
