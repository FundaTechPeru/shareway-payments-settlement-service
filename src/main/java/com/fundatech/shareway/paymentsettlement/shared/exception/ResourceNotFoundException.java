package com.fundatech.shareway.paymentsettlement.shared.exception;

/**
 * Indica que el recurso solicitado no existe o no pertenece al usuario autenticado.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
