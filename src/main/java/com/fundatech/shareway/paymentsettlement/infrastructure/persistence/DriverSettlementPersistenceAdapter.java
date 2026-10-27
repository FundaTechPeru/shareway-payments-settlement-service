package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import com.fundatech.shareway.paymentsettlement.domain.model.SettlementStatus;
import com.fundatech.shareway.paymentsettlement.domain.repository.DriverSettlementRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Adaptador JPA del puerto {@link DriverSettlementRepository}.
 */
@Repository
public class DriverSettlementPersistenceAdapter implements DriverSettlementRepository {

    private final JpaDriverSettlementRepository jpa;

    public DriverSettlementPersistenceAdapter(JpaDriverSettlementRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public DriverSettlement save(DriverSettlement settlement) {
        return jpa.save(settlement);
    }

    @Override
    public Optional<DriverSettlement> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<DriverSettlement> findByTripId(Long tripId) {
        return jpa.findByTripId(tripId);
    }

    @Override
    public Optional<DriverSettlement> findByIdAndDriverId(Long id, Long driverId) {
        return jpa.findByIdAndDriverId(id, driverId);
    }

    @Override
    public List<DriverSettlement> findByDriverId(Long driverId) {
        return jpa.findByDriverIdOrderByIdDesc(driverId);
    }

    @Override
    public List<DriverSettlement> findAll(Optional<SettlementStatus> status) {
        return status.map(jpa::findByStatusOrderByIdDesc).orElseGet(jpa::findAllByOrderByIdDesc);
    }
}
