package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.Event;
import com.svhelloworld.cdc.encounters.model.Encounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Normally, we would consume events coming off an SQS queue and ingest them. This class is a very simple proxy for that.
 */
@Component
public class EncounterEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(EncounterEventsConsumer.class);
    
    private final List<Event<Encounter>> eventsReceived;
    
    public EncounterEventsConsumer() {
        eventsReceived = new ArrayList<>();
    }
    
    public void handleEvent(Event<Encounter> event) {
        eventsReceived.add(event);
        log.debug("Event received: {}", event);
    }
    
    public List<Event<Encounter>> getEventsReceived() {
        return eventsReceived;
    }
    
    public Integer numberEventsReceived() {
        return eventsReceived.size();
    }
    
    /**
     * Clear events between test scenarios.
     */
    public void clearEvents() {
        eventsReceived.clear();
    }
    
    /**
     * The most recent event received
     * @return an Optional wrapper around an event
     */
    public Optional<Event<Encounter>> mostRecentEvent() {
        if (eventsReceived.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(eventsReceived.get(eventsReceived.size() - 1));
    }
}
