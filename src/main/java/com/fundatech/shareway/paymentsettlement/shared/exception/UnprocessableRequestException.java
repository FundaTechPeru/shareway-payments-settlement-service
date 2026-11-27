package com.fundatech.shareway.paymentsettlement.shared.exception;

/**
 * Indica que la solicitud es válida pero no se puede procesar con el estado actual, por ejemplo un pasajero sin
 * métodos de pago activos.
 */
public class UnprocessableRequestException extends RuntimeException {

    public UnprocessableRequestException(String message) {
        super(message);
    }
}
