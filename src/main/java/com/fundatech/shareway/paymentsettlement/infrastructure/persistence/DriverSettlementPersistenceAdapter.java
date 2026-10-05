package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.DriverSettlement;
import com.fundatech.shareway.paymentsettlement.domain.repository.DriverSettlementRepository;
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
    public Optional<DriverSettlement> findByTripId(Long tripId) {
        return jpa.findByTripId(tripId);
    }
}
