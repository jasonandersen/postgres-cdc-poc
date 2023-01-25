package com.svhelloworld.cdc.encounters;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Publishes events out to subscribers. Normally, this would be an SNS client to publish events out on a topic.
 * For the sake of this PoC, we're keeping it simple and just publishing events to Guava's in-memory event bus.
 */
@Component
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    
    private final EventBus eventBus;
    
    public EventPublisher() {
        eventBus = new EventBus();
        log.info("EventPublisher instantiated");
    }
    
    public void publish(List<?> events) {
        log.debug("Publishing {} events", events.size());
        events.forEach(eventBus::post);
    }
    
    public void publish(Object event) {
        log.debug("Publishing event");
        eventBus.post(event);
    }
    
    public void registerListener(Object listener) {
        eventBus.register(listener);
    }
}
