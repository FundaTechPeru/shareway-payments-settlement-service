package com.fundatech.shareway.paymentsettlement.infrastructure.security;

/**
 * Usuario autenticado construido únicamente a partir de los claims del JWT.
 */
public record AuthenticatedUser(Long userId, String email, UserRole role) {
}
