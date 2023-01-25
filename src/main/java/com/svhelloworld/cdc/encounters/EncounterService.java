package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.encounters.dao.EncounterDao;
import com.svhelloworld.cdc.encounters.dao.EncounterOutboxEntryDao;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntryBatch;
import com.svhelloworld.cdc.encounters.model.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncounterService {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterService.class);
    
    /**
     * How frequently we should be polling the encounters outbox to find data updates. Normally, we'd configure
     * this as part of application.properties but that isn't compatible with the @Scheduled annotation.
     */
    private static final int OUTBOX_POLLING_FREQUENCY = 2500;
    
    private final EncounterDao encounterDao;
    private final EncounterOutboxEntryDao outboxEntryDao;
    private final EventPublisher eventPublisher;
    
    public EncounterService(
            EncounterDao dao,
            EncounterOutboxEntryDao outboxEntryDao,
            EventPublisher eventPublisher) {
        this.encounterDao = dao;
        this.outboxEntryDao = outboxEntryDao;
        this.eventPublisher = eventPublisher;
    }
    
    public Encounter saveEncounter(Encounter encounter) {
        return encounterDao.save(encounter);
    }
    
    @Scheduled(fixedRate = OUTBOX_POLLING_FREQUENCY)
    public void pollEncounterOutbox() {
        log.debug("Polling encounters outbox");
        EncounterOutboxEntryBatch batch = new EncounterOutboxEntryBatch(encounterDao, outboxEntryDao);
        if (batch.entriesArePresent()) {
            log.debug("{} outbox entries discovered", batch.numberOfEntries());
            // TODO -- OPEN TRANSACTION BOUNDARY
            batch.resolveOutboxEntries();
            eventPublisher.publish(batch.getEvents());
            // TODO -- CLOSE TRANSACTION BOUNDARY
        }
    }
    
    /**
     * This method is used for testing only.
     * @return a list (possibly empty) of all unresolved outbox entries
     */
    public List<EncounterOutboxEntry> getUnresolvedOutboxEntries() {
        return outboxEntryDao.findByStatus(OutboxStatus.UNRESOLVED);
    }
    
    
}
