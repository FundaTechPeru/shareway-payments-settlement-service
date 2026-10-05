package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.Payment;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Adaptador JPA del puerto {@link PaymentRepository}.
 */
@Repository
public class PaymentPersistenceAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpa;

    public PaymentPersistenceAdapter(JpaPaymentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Payment save(Payment payment) {
        return jpa.save(payment);
    }

    @Override
    public Optional<Payment> findByBookingId(Long bookingId) {
        return jpa.findByBookingId(bookingId);
    }

    @Override
    public Optional<Payment> findByIdAndPassengerId(Long id, Long passengerId) {
        return jpa.findByIdAndPassengerId(id, passengerId);
    }

    @Override
    public List<Payment> findByPassengerId(Long passengerId) {
        return jpa.findByPassengerIdOrderByIdDesc(passengerId);
    }
}
