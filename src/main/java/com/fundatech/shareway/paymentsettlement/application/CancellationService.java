package com.fundatech.shareway.paymentsettlement.application;

import com.fundatech.shareway.paymentsettlement.domain.event.PaymentVoided;
import com.fundatech.shareway.paymentsettlement.domain.event.PenaltyApplied;
import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import com.fundatech.shareway.paymentsettlement.domain.model.Money;
import com.fundatech.shareway.paymentsettlement.domain.model.Payment;
import com.fundatech.shareway.paymentsettlement.domain.model.Penalty;
import com.fundatech.shareway.paymentsettlement.domain.policy.PenaltyPolicy;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentGateway;
import com.fundatech.shareway.paymentsettlement.domain.repository.PaymentRepository;
import com.fundatech.shareway.paymentsettlement.domain.repository.PenaltyRepository;
import com.fundatech.shareway.paymentsettlement.infrastructure.messaging.DomainEventPublisher;
import com.fundatech.shareway.paymentsettlement.shared.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resuelve el pago de una reserva cancelada: cobra la penalidad o anula la autorización (US12).
 */
@Service
public class CancellationService {

    private final PaymentRepository payments;
    private final PenaltyRepository penalties;
    private final PenaltyPolicy penaltyPolicy;
    private final PaymentGateway gateway;
    private final DomainEventPublisher events;

    public CancellationService(PaymentRepository payments, PenaltyRepository penalties, PenaltyPolicy penaltyPolicy,
                               PaymentGateway gateway, DomainEventPublisher events) {
        this.payments = payments;
        this.penalties = penalties;
        this.penaltyPolicy = penaltyPolicy;
        this.gateway = gateway;
        this.events = events;
    }

    /**
     * Resultado de la cancelación: el pago resultante y la penalidad, si se aplicó.
     */
    public record CancellationOutcome(Payment payment, Penalty penalty) {
    }

    /**
     * Aplica la {@link PenaltyPolicy} al pago AUTHORIZED de la reserva. Es idempotente por {@code bookingId}:
     * si la cancelación ya se resolvió devuelve el mismo resultado.
     *
     * @throws ResourceNotFoundException si la reserva no tiene pago
     * @throws com.fundatech.shareway.paymentsettlement.shared.exception.ConflictException si el pago no está AUTHORIZED
     */
    @Transactional
    public CancellationOutcome cancel(UUID bookingId, CancellationReason reason, Instant cancelledAt,
                                      Instant scheduledDepartureAt) {
        Payment payment = payments.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for booking " + bookingId));
        if (payment.isCancellationResolved()) {
            return new CancellationOutcome(payment, penalties.findByBookingId(bookingId).orElse(null));
        }
        Instant now = Instant.now();
        Optional<Money> penaltyAmount = penaltyPolicy.penaltyFor(reason, cancelledAt, scheduledDepartureAt,
                payment.getAuthorizedAmount());
        if (penaltyAmount.isEmpty()) {
            payment.voidAuthorization(reason, now);
            gateway.voidAuthorization(payment.getProviderRef());
            Payment voided = payments.save(payment);
            events.publish(new PaymentVoided(voided.getId(), bookingId, reason, now));
            return new CancellationOutcome(voided, null);
        }
        Money amount = penaltyAmount.get();
        payment.chargePenalty(amount, reason, now);
        gateway.capture(payment.getProviderRef(), amount);
        Payment charged = payments.save(payment);
        Penalty penalty = penalties.save(Penalty.applyTo(charged, reason, amount, now));
        events.publish(new PenaltyApplied(penalty.getId(), charged.getId(), bookingId, charged.getPassengerId(),
                amount.getAmount(), amount.getCurrency(), reason, now));
        return new CancellationOutcome(charged, penalty);
    }
}
