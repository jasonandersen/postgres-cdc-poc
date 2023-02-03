package com.svhelloworld.cdc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;
import java.util.UUID;

/**
 * Basic event to transport a message.
 * @param <T> type representing the body of the event.
 */
public class Event<T extends Object> {
    
    private final UUID eventId;
    private final Instant timestamp;
    private final String eventType;
    private final T body;
    
    public Event(UUID eventId, Instant timestamp, String eventType, T body) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.body = body;
    }
    
    public UUID getEventId() {
        return eventId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public T getBody() {
        return body;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        
    }
}
