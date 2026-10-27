package com.fundatech.shareway.paymentsettlement.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Penalidad cobrada al pasajero por cancelar tarde o no presentarse.
 */
@Entity
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CancellationReason reason;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", precision = 12, scale = 2,
            nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    private Money amount;

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;

    protected Penalty() {
    }

    /**
     * Registra la penalidad cobrada sobre el pago de la reserva cancelada.
     */
    public static Penalty applyTo(Payment payment, CancellationReason reason, Money amount, Instant at) {
        Penalty penalty = new Penalty();
        penalty.paymentId = payment.getId();
        penalty.bookingId = payment.getBookingId();
        penalty.passengerId = payment.getPassengerId();
        penalty.reason = reason;
        penalty.amount = amount;
        penalty.appliedAt = at;
        return penalty;
    }

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public CancellationReason getReason() {
        return reason;
    }

    public Money getAmount() {
        return amount;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }
}
