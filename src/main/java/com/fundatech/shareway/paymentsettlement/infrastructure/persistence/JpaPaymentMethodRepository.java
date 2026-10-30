package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de la tabla payment_methods.
 */
public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    List<PaymentMethod> findByPassengerIdAndActiveTrueOrderByCreatedAtAsc(UUID passengerId);

    boolean existsByPassengerIdAndCardTokenAndActiveTrue(UUID passengerId, String cardToken);
}
