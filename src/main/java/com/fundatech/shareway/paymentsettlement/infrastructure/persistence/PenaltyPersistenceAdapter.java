package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.Penalty;
import com.fundatech.shareway.paymentsettlement.domain.repository.PenaltyRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador JPA del puerto {@link PenaltyRepository}.
 */
@Repository
public class PenaltyPersistenceAdapter implements PenaltyRepository {

    private final JpaPenaltyRepository jpa;

    public PenaltyPersistenceAdapter(JpaPenaltyRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Penalty save(Penalty penalty) {
        return jpa.save(penalty);
    }

    @Override
    public Optional<Penalty> findByBookingId(UUID bookingId) {
        return jpa.findByBookingId(bookingId);
    }
}
