package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.Event;
import com.svhelloworld.cdc.encounters.model.Encounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Publishes events out to subscribers. Normally, this would be an SNS client to publish events out on a topic.
 * For the sake of this PoC, we're keeping it simple and just writing events directly to the consumer.
 */
@Component
public class EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);
    
    private final EncounterEventsConsumer consumer;
    
    public EventPublisher(EncounterEventsConsumer consumer) {
        this.consumer = consumer;
        log.info("EventPublisher instantiated");
    }
    
    public void publish(List<Event<Encounter>> events) {
        log.debug("Publishing {} events", events.size());
        events.forEach(consumer::handleEvent);
    }
    
    public void publish(Event event) {
        log.debug("Publishing event");
        consumer.handleEvent(event);
    }

}
