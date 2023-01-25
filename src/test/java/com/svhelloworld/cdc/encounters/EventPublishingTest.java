package com.svhelloworld.cdc.encounters;

import com.google.common.eventbus.Subscribe;
import com.svhelloworld.cdc.Event;
import com.svhelloworld.cdc.encounters.model.Encounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventPublishingTest {
    
    @Autowired
    private EventPublisher eventPublisher;
    private final List<Event<?>> events = new ArrayList<>();
    
    @BeforeEach
    public void registerListener() {
        eventPublisher.registerListener(this);
    }
    
    @Subscribe
    public void handleEvent(Event<?> event) {
        events.add(event);
    }
    
    @Test
    void dependencyInjection() {
        assertNotNull(eventPublisher);
    }
    
    @Test
    void sendAndReceiveEvent() throws InterruptedException {
        Encounter encounter = new Encounter();
        Event<Encounter> event = new Event<>(
                UUID.randomUUID(),
                Instant.now(),
                "TestEvent",
                encounter);
        eventPublisher.publish(event);
        Thread.sleep(300);
        assertFalse(events.isEmpty());
    }
}
