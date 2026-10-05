package com.fundatech.shareway.paymentsettlement.infrastructure.config;

import com.fundatech.shareway.paymentsettlement.domain.policy.CommissionPolicy;
import com.fundatech.shareway.paymentsettlement.domain.policy.PercentageCommissionPolicy;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Construye las políticas de dominio con los parámetros definidos en application.properties.
 */
@Configuration
public class DomainPolicyConfig {

    @Bean
    public CommissionPolicy commissionPolicy(@Value("${app.settlement.commission-rate}") BigDecimal rate) {
        return new PercentageCommissionPolicy(rate);
    }
}
