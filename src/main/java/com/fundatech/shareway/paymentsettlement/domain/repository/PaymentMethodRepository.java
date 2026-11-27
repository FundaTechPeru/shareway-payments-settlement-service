package com.fundatech.shareway.paymentsettlement.domain.repository;

import com.fundatech.shareway.paymentsettlement.domain.model.PaymentMethod;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de los métodos de pago.
 */
public interface PaymentMethodRepository {

    PaymentMethod save(PaymentMethod paymentMethod);

    Optional<PaymentMethod> findById(UUID id);

    List<PaymentMethod> findActiveByPassengerId(UUID passengerId);

    /**
     * Devuelve el método de pago activo registrado más recientemente por el pasajero.
     */
    Optional<PaymentMethod> findLatestActiveByPassengerId(UUID passengerId);

    boolean existsActiveByPassengerIdAndCardToken(UUID passengerId, String cardToken);
}
