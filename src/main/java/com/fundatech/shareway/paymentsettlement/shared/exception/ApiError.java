package com.fundatech.shareway.paymentsettlement.shared.exception;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Cuerpo uniforme de error devuelto por todos los endpoints del servicio.
 */
public record ApiError(Instant timestamp, int status, String error, String message, String path,
                       List<FieldError> fieldErrors) {

    /**
     * Error asociado a un campo concreto de la petición.
     */
    public record FieldError(String field, String message) {
    }

    /**
     * Construye un error sin detalle de campos para el estado HTTP indicado.
     */
    public static ApiError of(HttpStatus status, String message, String path) {
        return of(status, message, path, List.of());
    }

    /**
     * Construye un error con la lista de campos inválidos.
     */
    public static ApiError of(HttpStatus status, String message, String path, List<FieldError> fieldErrors) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }
}
