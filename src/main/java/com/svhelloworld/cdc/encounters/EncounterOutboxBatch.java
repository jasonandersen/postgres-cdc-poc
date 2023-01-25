package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.Event;
import com.svhelloworld.cdc.encounters.dao.EncounterDao;
import com.svhelloworld.cdc.encounters.dao.EncounterOutboxEntryDao;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A batch of unresolved {@link EncounterOutboxEntry}s to turn into {@link Event}s.
 */
public class EncounterOutboxBatch {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterOutboxBatch.class);
    private static final String EVENT_TYPE = "EncounterUpdatedEvent";
    
    private final EncounterDao encounterDao;
    private final EncounterOutboxEntryDao outboxEntryDao;
    private final List<EncounterOutboxEntry> entries;
    private final Map<Long, List<EncounterOutboxEntry>> entriesById;
    
    public EncounterOutboxBatch(
            EncounterDao encounterDao,
            EncounterOutboxEntryDao outboxEntryDao) {
        
        this.encounterDao = encounterDao;
        this.outboxEntryDao = outboxEntryDao;
        this.entries = outboxEntryDao.findByStatus(OutboxStatus.UNRESOLVED);
        this.entriesById = entriesById();
        if (entriesArePresent()) {
            log.debug("{} encounter mutations discovered.", entriesById.size());
        } else {
            log.debug("No encounter mutations discovered.");
        }
    }
    
    /**
     * @return true if unresolved {@link EncounterOutboxEntry}s were found.
     */
    public boolean entriesArePresent() {
        return !entriesById.isEmpty();
    }
    
    /**
     * @return the number of unresolved outbox entries discovered
     */
    public int numberOfEntries() {
        return entries.size();
    }
    
    /**
     * @return a list of {@link Event}s constructed from {@link EncounterOutboxEntry}s and {@link Encounter}s.
     */
    public List<Event<Encounter>> getEvents() {
        List<Event<Encounter>> events = new LinkedList<>();
        if (entriesArePresent()) {
            Iterable<Encounter> encounters = encounterDao.findAllById(entriesById.keySet());
            encounters.forEach(
                    e -> events.add(buildEvent(e, entriesById.get(e.getId())))
            );
        }
        log.debug("Built {} events", events.size());
        return events;
    }
    
    /**
     * Mark all the unresolved entries as RESOLVED.
     */
    public void resolveOutboxEntries() {
        entries.forEach(e -> e.setStatus(OutboxStatus.RESOLVED));
        outboxEntryDao.saveAll(entries);
        log.debug("Marked {} outbox entries as RESOLVED", entries.size());
    }
    
    private Event<Encounter> buildEvent(Encounter encounter, List<EncounterOutboxEntry> outboxEntries) {
        EncounterOutboxEntry mostRecent = findMostRecentOutboxEntry(outboxEntries);
        return new Event<>(mostRecent.getEventId(), mostRecent.getCreatedOn(), EVENT_TYPE, encounter);
    }
    
    /**
     * If there are multiple {@link EncounterOutboxEntry}s for a single {@link Encounter}, we'll use the ID and
     * timestamp from the most recent entry.
     */
    private EncounterOutboxEntry findMostRecentOutboxEntry(List<EncounterOutboxEntry> outboxEntries) {
        Optional<EncounterOutboxEntry> result = outboxEntries
                .stream()
                .max(Comparator.comparing(EncounterOutboxEntry::getCreatedOn));
        if (result.isPresent()) {
            return result.get();
        }
        throw new IllegalArgumentException("No outbox entries were discovered.");
    }
    
    /**
     * Organizes all the {@link EncounterOutboxEntry}s into a map keyed by encounter ID.
     */
    private Map<Long, List<EncounterOutboxEntry>> entriesById() {
        Map<Long, List<EncounterOutboxEntry>> byId = new HashMap<>();
        for (EncounterOutboxEntry entry : entries) {
            if (byId.containsKey(entry.getEncounterId())) {
                // key and list already exist
                byId.get(entry.getEncounterId()).add(entry);
            } else {
                // key does not exist so we have to create list
                byId.put(entry.getEncounterId(), new LinkedList<>());
                byId.get(entry.getEncounterId()).add(entry);
            }
        }
        return byId;
    }
}
