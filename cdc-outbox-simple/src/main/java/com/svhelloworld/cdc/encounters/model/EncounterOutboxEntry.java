package com.svhelloworld.cdc.encounters.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;
import java.util.UUID;

/**
 * Any time a record is inserted or updated in the underlying tables that store {@link Encounter} aggregates, the
 * change will be record in the encounters_outbox table. This entity represents an outbox entry.
 */
@Entity
@Table(name = "encounters_outbox")
public class EncounterOutboxEntry {
    
    public static EncounterOutboxEntry fromEncounterId(long encounterId) {
        EncounterOutboxEntry entry = new EncounterOutboxEntry();
        entry.setEventId(UUID.randomUUID());
        entry.setEncounterId(encounterId);
        entry.setStatus(OutboxStatus.UNRESOLVED);
        entry.setCreatedOn(Instant.now());
        return entry;
    }
    
    @Id
    @Column(name = "event_id")
    private UUID eventId;
    
    @Column(name = "encounter_id")
    private Long encounterId;
    
    @Column(name = "outbox_status_id")
    private OutboxStatus status;
    
    @Column(name = "created_on")
    private Instant createdOn;
    
    public UUID getEventId() {
        return eventId;
    }
    
    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
    
    public Long getEncounterId() {
        return encounterId;
    }
    
    public void setEncounterId(Long encounterId) {
        this.encounterId = encounterId;
    }
    
    public OutboxStatus getStatus() {
        return status;
    }
    
    public void setStatus(OutboxStatus status) {
        this.status = status;
    }
    
    public Instant getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncounterOutboxEntry)) return false;
        EncounterOutboxEntry that = (EncounterOutboxEntry) o;
        return getEventId().equals(that.getEventId());
    }
    
    @Override
    public int hashCode() {
        return getEventId().hashCode();
    }
}
