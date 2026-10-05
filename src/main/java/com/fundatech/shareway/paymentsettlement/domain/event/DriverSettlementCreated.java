package com.fundatech.shareway.paymentsettlement.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Se generó la liquidación pendiente de un conductor por un viaje completado.
 */
public record DriverSettlementCreated(Long settlementId, Long tripId, Long driverId, BigDecimal gross,
                                      BigDecimal commission, BigDecimal net, String currency, Instant occurredAt)
        implements DomainEvent {
}
