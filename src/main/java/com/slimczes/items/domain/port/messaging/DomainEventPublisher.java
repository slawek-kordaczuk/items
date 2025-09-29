package com.slimczes.items.domain.port.messaging;

import java.util.List;

import com.slimczes.items.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
