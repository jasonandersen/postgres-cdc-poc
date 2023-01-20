package com.svhelloworld.cdc;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EncounterDaoTest {
    private static final Logger log = LoggerFactory.getLogger(EncounterDaoTest.class);
    
    @Autowired
    private EncounterDao encounterDao;
    
    @Test
    void dependencyInjectionConfigured() {
        assertNotNull(encounterDao);
    }
    
    @Test
    void save() {
        long originalCount = encounterDao.count();
        log.info("{} rows in encounter table", originalCount);
        
        encounterDao.save(encounter());
        
        long newCount = encounterDao.count();
        log.info("{} rows in encounter table after save", newCount);
        assertEquals(originalCount + 1, newCount);
    }
    
    private Encounter encounter() {
        Encounter encounter = new Encounter();
        encounter.setNotes("I like monkeys!");
        encounter.setStatusId(100);
        return encounter;
    }
}
