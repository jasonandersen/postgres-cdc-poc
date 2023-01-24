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
 * This entity represents a change in the underlying tables representing the aggregate {@link Encounter}.
 */
@Entity
@Table(name = "encounters_outbox")
public class EncounterOutboxEntry {
    
    @Id
    @Column(name = "event_id")
    private UUID eventId;
    
    @Column(name = "encounter_id")
    private Integer encounterId;
    
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
    
    public Integer getEncounterId() {
        return encounterId;
    }
    
    public void setEncounterId(Integer encounterId) {
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
