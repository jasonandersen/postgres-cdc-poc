package com.svhelloworld.cdc.encounters;

import java.util.stream.Stream;

/**
 * Status of an encounter
 */
public enum EncounterStatus {
    NEW(100),
    IN_PROGRESS(200),
    COMPLETE(300);
    
    /**
     * Retrieve an instance from it's statusId.
     */
    public static EncounterStatus fromId(int statusId) {
        return Stream.of(EncounterStatus.values())
                .filter(s -> s.getStatusId() == statusId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
    
    private final int statusId;
    
    EncounterStatus(int statusId) {
        this.statusId = statusId;
    }
    
    public int getStatusId() {
        return statusId;
    }
}
