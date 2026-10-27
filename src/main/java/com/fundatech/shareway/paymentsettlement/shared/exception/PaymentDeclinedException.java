package com.fundatech.shareway.paymentsettlement.shared.exception;

/**
 * Indica que la pasarela de pagos rechazó la transacción.
 */
public class PaymentDeclinedException extends RuntimeException {

    public PaymentDeclinedException(String message) {
        super(message);
    }
}
