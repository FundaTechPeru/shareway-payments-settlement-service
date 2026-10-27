package com.fundatech.shareway.paymentsettlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio Payments &amp; Settlement de ShareWay.
 */
@SpringBootApplication
public class PaymentSettlementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentSettlementApplication.class, args);
    }
}
