package com.svhelloworld.cdc.encounters.model;

import java.util.stream.Stream;

/**
 * Indicates the status of a {@link EncounterOutboxEntry}.
 * <ul>
 *     <li>[100] UNRESOLVED - this entry has not been thrown out as an event yet</li>
 *     <li>[200] RESOLVED - this entry has been thrown out as an event</li>
 *     <li>[300] ERROR - there was an error attempting to throw this entry as an event</li>
 * </ul>
 */
public enum OutboxStatus {
    UNRESOLVED(100),
    RESOLVED(200),
    ERROR(300);
    
    /**
     * Retrieve an instance from it's statusId.
     */
    public static OutboxStatus fromId(int statusId) {
        return Stream.of(OutboxStatus.values())
                .filter(s -> s.getStatusId() == statusId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
    
    private final int statusId;
    
    OutboxStatus(int statusId) {
        this.statusId = statusId;
    }
    
    public int getStatusId() {
        return statusId;
    }
}
