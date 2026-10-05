package com.fundatech.shareway.paymentsettlement.infrastructure.messaging;

import com.fundatech.shareway.paymentsettlement.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Registra en el log cada evento de dominio publicado.
 */
@Component
public class DomainEventLoggingListener {

    private static final Logger log = LoggerFactory.getLogger(DomainEventLoggingListener.class);

    @EventListener
    public void on(DomainEvent event) {
        log.info("Domain event {}: {}", event.getClass().getSimpleName(), event);
    }
}
