package com.fundatech.shareway.paymentsettlement.infrastructure.persistence;

import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data de la tabla payment_methods.
 */
public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findByPassengerIdAndActiveTrueOrderByIdAsc(Long passengerId);

    boolean existsByPassengerIdAndCardTokenAndActiveTrue(Long passengerId, String cardToken);
}
