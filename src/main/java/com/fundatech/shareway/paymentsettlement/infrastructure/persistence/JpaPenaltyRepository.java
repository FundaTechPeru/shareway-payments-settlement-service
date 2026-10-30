package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.Penalty;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de la tabla penalties.
 */
public interface JpaPenaltyRepository extends JpaRepository<Penalty, UUID> {

    Optional<Penalty> findByBookingId(UUID bookingId);
}
