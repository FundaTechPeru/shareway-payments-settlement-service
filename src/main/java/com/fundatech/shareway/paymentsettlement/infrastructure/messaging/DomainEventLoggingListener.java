package com.fundatech.shareway.paymentsettlement.infrastructure.messaging;

import com.fundatech.shareway.paymentsettlement.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Registra en el log cada evento de dominio una vez confirmada la transacción que lo produjo.
 */
@Component
public class DomainEventLoggingListener {

    private static final Logger log = LoggerFactory.getLogger(DomainEventLoggingListener.class);

    @TransactionalEventListener(fallbackExecution = true)
    public void on(DomainEvent event) {
        log.info("Domain event {}: {}", event.getClass().getSimpleName(), event);
    }
}
