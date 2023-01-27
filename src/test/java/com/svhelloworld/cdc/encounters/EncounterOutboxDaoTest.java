package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.encounters.dao.EncounterOutboxEntryDao;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.OutboxStatus;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EncounterOutboxDaoTest {
    private static final Logger log = LoggerFactory.getLogger(EncounterOutboxDaoTest.class);
    
    @Autowired
    private EncounterOutboxEntryDao dao;
    
    @Test
    void dependencyInjection() {
        assertNotNull(dao);
    }
    
    @Test
    public void findAllUnresolvedEntries() {
        List<EncounterOutboxEntry> entries = dao.findByStatus(OutboxStatus.UNRESOLVED);
        assertNotNull(entries);
        assertFalse(entries.isEmpty());
        entries.forEach(e -> log.info("Outbox entry: {}", e));
    }
}
