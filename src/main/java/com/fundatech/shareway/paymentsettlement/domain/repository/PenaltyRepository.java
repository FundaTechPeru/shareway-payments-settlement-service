package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.Penalty;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de las penalidades.
 */
public interface PenaltyRepository {

    Penalty save(Penalty penalty);

    Optional<Penalty> findByBookingId(UUID bookingId);
}
