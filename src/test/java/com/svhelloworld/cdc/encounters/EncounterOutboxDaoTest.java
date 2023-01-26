package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.encounters.dao.EncounterOutboxEntryDao;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.OutboxStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EncounterOutboxDaoTest {
    private static final Logger log = LoggerFactory.getLogger(EncounterOutboxDaoTest.class);
    
    @Autowired
    private EncounterOutboxEntryDao dao;
    
    @Test
    void dependencyInjection() {
        assertNotNull(dao);
    }
    
    @Disabled
    @Test
    void findById() {
        UUID id = UUID.fromString("8b962ed6-9b99-11ed-9699-0242ac120003");
        Optional<EncounterOutboxEntry> result = dao.findById(id);
        if (result.isPresent()) {
            EncounterOutboxEntry entry = result.get();
            assertNotNull(entry);
        } else {
            fail("Outbox entry didn't load.");
        }
    }
    
    @Test
    public void findAllUnresolvedEntries() {
        List<EncounterOutboxEntry> entries = dao.findByStatus(OutboxStatus.UNRESOLVED);
        assertNotNull(entries);
        assertFalse(entries.isEmpty());
        entries.forEach(e -> log.info("Outbox entry: {}", e));
    }
}
