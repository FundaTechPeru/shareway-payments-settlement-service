package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de los métodos de pago.
 */
public interface PaymentMethodRepository {

    PaymentMethod save(PaymentMethod paymentMethod);

    Optional<PaymentMethod> findById(Long id);

    List<PaymentMethod> findActiveByPassengerId(Long passengerId);

    boolean existsActiveByPassengerIdAndCardToken(Long passengerId, String cardToken);
}
