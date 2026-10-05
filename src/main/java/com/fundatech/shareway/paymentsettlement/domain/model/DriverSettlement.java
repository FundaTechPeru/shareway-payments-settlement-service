package com.fundatech.shareway.paymentsettlement.domain.model;

import com.fundatech.shareway.paymentsettlement.domain.policy.CommissionPolicy;
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
 * Liquidación de las ganancias de un conductor por un viaje completado. Invariante: bruto = comisión + neto.
 */
@Entity
@Table(name = "driver_settlements")
public class DriverSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false, unique = true)
    private Long tripId;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "gross_amount", precision = 12, scale = 2,
            nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    private Money gross;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "commission_amount", precision = 12, scale = 2,
            nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "commission_currency", length = 3,
            nullable = false))
    private Money commission;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "net_amount", precision = 12, scale = 2,
            nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "net_currency", length = 3, nullable = false))
    private Money net;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "trip_completed_at", nullable = false)
    private Instant tripCompletedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    protected DriverSettlement() {
    }

    /**
     * Crea una liquidación PENDING calculando la comisión con la política y el neto como bruto menos comisión.
     */
    public static DriverSettlement create(Long tripId, Long driverId, Money gross, Instant tripCompletedAt,
                                          CommissionPolicy commissionPolicy) {
        DriverSettlement settlement = new DriverSettlement();
        settlement.tripId = tripId;
        settlement.driverId = driverId;
        settlement.gross = gross;
        settlement.commission = commissionPolicy.commissionFor(gross);
        settlement.net = gross.subtract(settlement.commission);
        settlement.status = SettlementStatus.PENDING;
        settlement.tripCompletedAt = tripCompletedAt;
        settlement.createdAt = Instant.now();
        return settlement;
    }

    public Long getId() {
        return id;
    }

    public Long getTripId() {
        return tripId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Money getGross() {
        return gross;
    }

    public Money getCommission() {
        return commission;
    }

    public Money getNet() {
        return net;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public Instant getTripCompletedAt() {
        return tripCompletedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPaidAt() {
        return paidAt;
    }
}
