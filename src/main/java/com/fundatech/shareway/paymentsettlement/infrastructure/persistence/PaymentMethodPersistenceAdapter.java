package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentMethodRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Adaptador JPA del puerto {@link PaymentMethodRepository}.
 */
@Repository
public class PaymentMethodPersistenceAdapter implements PaymentMethodRepository {

    private final JpaPaymentMethodRepository jpa;

    public PaymentMethodPersistenceAdapter(JpaPaymentMethodRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return jpa.save(paymentMethod);
    }

    @Override
    public Optional<PaymentMethod> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<PaymentMethod> findActiveByPassengerId(Long passengerId) {
        return jpa.findByPassengerIdAndActiveTrueOrderByIdAsc(passengerId);
    }

    @Override
    public boolean existsActiveByPassengerIdAndCardToken(Long passengerId, String cardToken) {
        return jpa.existsByPassengerIdAndCardTokenAndActiveTrue(passengerId, cardToken);
    }
}
