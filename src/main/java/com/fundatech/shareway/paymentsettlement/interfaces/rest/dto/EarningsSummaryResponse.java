package com.fundatech.shareway.paymentsettlement.interfaces.rest.dto;

import com.fundatech.shareway.paymentsettlement.application.SettlementService.EarningsSummary;
import java.math.BigDecimal;

/**
 * Resumen de ganancias del conductor autenticado.
 */
public record EarningsSummaryResponse(BigDecimal totalGross, BigDecimal totalCommission, BigDecimal totalNet,
                                      BigDecimal pendingNet, BigDecimal paidNet) {

    public static EarningsSummaryResponse from(EarningsSummary summary) {
        return new EarningsSummaryResponse(summary.totalGross(), summary.totalCommission(), summary.totalNet(),
                summary.pendingNet(), summary.paidNet());
    }
}
