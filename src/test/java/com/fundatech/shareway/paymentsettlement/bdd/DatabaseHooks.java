package com.fundatech.shareway.paymentsettlement.bdd;

import io.cucumber.java.Before;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Vacía todas las tablas antes de cada escenario para que sean independientes.
 */
public class DatabaseHooks {

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    public DatabaseHooks(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Before
    public void cleanDatabase() {
        transactionTemplate.executeWithoutResult(status -> {
            for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
                entityManager.createQuery("delete from " + entity.getName()).executeUpdate();
            }
        });
    }
}
