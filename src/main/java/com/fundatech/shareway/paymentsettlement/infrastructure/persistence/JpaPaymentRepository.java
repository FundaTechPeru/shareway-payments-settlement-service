package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de la tabla payments.
 */
public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByIdAndPassengerId(Long id, Long passengerId);

    List<Payment> findByPassengerIdOrderByIdDesc(Long passengerId);
}
