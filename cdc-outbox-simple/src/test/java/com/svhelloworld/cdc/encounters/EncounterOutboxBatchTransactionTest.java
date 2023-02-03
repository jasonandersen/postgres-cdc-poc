package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.EventPublisher;
import com.svhelloworld.cdc.encounters.dao.EncounterDao;
import com.svhelloworld.cdc.encounters.dao.EncounterOutboxEntryDao;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import com.svhelloworld.cdc.encounters.model.OutboxStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Validate that marking outbox entries as RESOLVED and publishing the associated events will happen in an atomic
 * transaction. We need to guarantee that both actions will either succeed or fail in unison.
 */
@SuppressWarnings("unchecked")
@SpringBootTest
public class EncounterOutboxBatchTransactionTest {
    @Autowired
    private EncounterDao encounterDao;
    @Autowired
    private EncounterOutboxEntryDao outboxEntryDao;
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private EncounterEventsConsumer eventsConsumer;
    
    @BeforeEach
    public void setup() {
        assertNoUnresolvedOutboxEntries();
        setupEncounter();
        // inserting an encounter will create an unresolved outbox entry
        assertUnresolvedOutboxEntries(1);
        eventsConsumer.clearEvents();
    }
    
    @AfterEach
    public void teardown() {
        deleteUnresolvedOutboxEntries();
        assertNoUnresolvedOutboxEntries();
        eventsConsumer.clearEvents();
    }
    
    @Test
    public void publishAndResolveHappyPath() {
        assertEquals(0, eventsConsumer.numberEventsReceived());
        assertUnresolvedOutboxEntries(1);
        EncounterOutboxBatch batch = new EncounterOutboxBatch(encounterDao, outboxEntryDao, eventPublisher);
        
        batch.publishAndResolve();
        
        assertNoUnresolvedOutboxEntries();
        assertEquals(1, eventsConsumer.numberEventsReceived());
    }
    
    @Test
    public void publishAndResolveDbException() {
        // set up the outbox DAO to throw an exception
        EncounterOutboxEntryDao mockDao = mock(EncounterOutboxEntryDao.class);
        when(
                mockDao.saveAll(isA(List.class)))
                .thenThrow(new IllegalArgumentException("database exception"));
        
        EncounterOutboxBatch batch = new EncounterOutboxBatch(encounterDao, mockDao, eventPublisher);
        try {
            batch.publishAndResolve();
        } catch (Exception e) {
            //ignore
        }
        
        assertEquals(0, eventsConsumer.numberEventsReceived());
        assertUnresolvedOutboxEntries(1);
    }
    
    @Test
    public void publishAndResolveEventPublishException() {
        assertUnresolvedOutboxEntries(1);
        // set up event publisher to throw exception
        EventPublisher mockPublisher = mock(EventPublisher.class);
        doThrow(
                new IllegalArgumentException("event publishing exception"))
                .when(mockPublisher)
                .publish(isA(List.class));
    
        EncounterOutboxBatch batch = new EncounterOutboxBatch(encounterDao, outboxEntryDao, mockPublisher);
        try {
            batch.publishAndResolve();
        } catch (Exception e) {
            //ignore
        }
    
        assertEquals(0, eventsConsumer.numberEventsReceived());
        assertUnresolvedOutboxEntries(1);
    }
    
    private void assertNoUnresolvedOutboxEntries() {
        assertUnresolvedOutboxEntries(0);
    }
    
    private void assertUnresolvedOutboxEntries(int expectedNumber) {
        assertEquals(expectedNumber, outboxEntryDao.findByStatus(OutboxStatus.UNRESOLVED).size());
    }
    
    private void setupEncounter() {
        Encounter encounter = new Encounter();
        encounter.setPatientId(UUID.randomUUID().toString());
        encounter.setNotes("blah blah blah");
        encounter.setStatus(EncounterStatus.NEW);
        encounterDao.save(encounter);
    }
    
    private void deleteUnresolvedOutboxEntries() {
        outboxEntryDao.deleteAll(outboxEntryDao.findByStatus(OutboxStatus.UNRESOLVED));
    }
}
