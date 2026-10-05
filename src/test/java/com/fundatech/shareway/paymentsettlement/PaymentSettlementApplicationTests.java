package com.fundatech.shareway.paymentsettlement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifica que el contexto de Spring arranque con el perfil de pruebas.
 */
@SpringBootTest
@ActiveProfiles("test")
class PaymentSettlementApplicationTests {

    @Test
    void contextLoads() {
    }
}
