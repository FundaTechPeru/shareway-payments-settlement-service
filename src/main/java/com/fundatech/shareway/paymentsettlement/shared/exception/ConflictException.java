package com.fundatech.shareway.paymentsettlement.shared.exception;

/**
 * Indica un registro duplicado o una transición de estado no permitida.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
