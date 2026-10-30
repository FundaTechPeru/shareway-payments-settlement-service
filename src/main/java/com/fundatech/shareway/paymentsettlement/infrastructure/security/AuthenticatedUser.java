package com.fundatech.shareway.paymentsettlement.infrastructure.security;

import java.util.UUID;

/**
 * Usuario autenticado construido únicamente a partir de los claims del JWT.
 */
public record AuthenticatedUser(UUID userId, String email, UserRole role) {
}
