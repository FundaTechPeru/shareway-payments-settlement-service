package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import com.fundatech.shareway.paymentsettlement.application.CancellationService.CancellationOutcome;
import com.fundatech.shareway.paymentsettlement.domain.model.CancellationReason;
import com.fundatech.shareway.paymentsettlement.domain.model.PaymentStatus;
import java.math.BigDecimal;

/**
 * Resultado de procesar una cancelación: estado final del pago y penalidad cobrada, si hubo.
 */
public record CancellationResponse(Long bookingId, Long paymentId, PaymentStatus paymentStatus,
                                   CancellationReason reason, boolean penaltyApplied, Long penaltyId,
                                   BigDecimal penaltyAmount, String currency) {

    public static CancellationResponse from(CancellationOutcome outcome) {
        var payment = outcome.payment();
        var penalty = outcome.penalty();
        return new CancellationResponse(payment.getBookingId(), payment.getId(), payment.getStatus(),
                payment.getCancellationReason(), penalty != null, penalty == null ? null : penalty.getId(),
                penalty == null ? BigDecimal.ZERO.setScale(2) : penalty.getAmount().getAmount(),
                payment.getAuthorizedAmount().getCurrency());
    }
}
