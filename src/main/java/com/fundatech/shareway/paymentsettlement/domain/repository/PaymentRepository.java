package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.Payment;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de los pagos.
 */
public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByIdAndPassengerId(Long id, Long passengerId);

    List<Payment> findByPassengerId(Long passengerId);
}
