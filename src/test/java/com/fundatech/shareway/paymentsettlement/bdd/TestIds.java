package com.fundatech.shareway.paymentsettlement.bdd;

import java.util.UUID;

/**
 * Traduce los alias numéricos usados en los escenarios a UUID deterministas.
 */
public final class TestIds {

    private TestIds() {
    }

    public static UUID uuid(long alias) {
        return new UUID(0L, alias);
    }
}
