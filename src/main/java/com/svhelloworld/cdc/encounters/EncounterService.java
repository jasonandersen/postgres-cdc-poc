package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.encounters.dao.EncounterDao;
import com.svhelloworld.cdc.encounters.model.Encounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EncounterService {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterService.class);
    
    private final EncounterDao dao;
    
    public EncounterService(EncounterDao dao) {
        this.dao = dao;
    }
    
    public Encounter saveEncounter(Encounter encounter) {
        log.info("Saving encounter: {}", encounter);
        return dao.save(encounter);
    }
    
    public Optional<Encounter> findEncounterById(long id) {
        log.info("Retrieving encounter by ID: {}", id);
        return dao.findById(id);
    }
}
