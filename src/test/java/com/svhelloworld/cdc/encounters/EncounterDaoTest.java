package com.svhelloworld.cdc.encounters;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validating that {@link Encounter} entities are persisted correctly.
 */
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
    void saveWithNoProcedureOrDxCodes() {
        long originalCount = encounterDao.count();
        log.info("{} rows in encounter table", originalCount);
        
        encounterDao.save(buildEncounter());
        
        long newCount = encounterDao.count();
        log.info("{} rows in encounter table after save", newCount);
        assertEquals(originalCount + 1, newCount);
    }
    
    @Test
    void modifyStatus() {
        // save as NEW encounter
        Encounter newEncounter = buildEncounter();
        Encounter savedEncounter = encounterDao.save(newEncounter);
        assertEquals(EncounterStatus.NEW, savedEncounter.getStatus());
        
        // update to IN_PROGRESS
        savedEncounter.setStatus(EncounterStatus.IN_PROGRESS);
        Encounter updatedEncounter = encounterDao.save(savedEncounter);
    
        // assert results
        Optional<Encounter> result = encounterDao.findById(updatedEncounter.getId());
        if (result.isPresent()) {
            Encounter reloadedEncounter = result.get();
            assertEquals(EncounterStatus.IN_PROGRESS, reloadedEncounter.getStatus());
        } else {
            fail(); // shouldn't get to here
        }
        
    }
    
    @Test
    void saveWithProcedureCodes() {
        // new encounter
        Encounter newEncounter = buildEncounter();
        newEncounter.addProcedure(ProcedureCode.from("86930", "Frozen blood prep"));
        // save encounter
        Encounter savedEncounter = encounterDao.save(newEncounter);
        // re-load encounter from database
        Optional<Encounter> result = encounterDao.findById(savedEncounter.getId());
        if (result.isPresent()) {
            // assert results
            Encounter retrievedEncounter = result.get();
            assertEquals(1, retrievedEncounter.getProcedureCodes().size());
            ProcedureCode procedureCode = retrievedEncounter.getProcedureCodes().iterator().next();
            assertEquals("86930", procedureCode.getCptCode());
            assertEquals("Frozen blood prep", procedureCode.getDescription());
        } else {
            fail("Encounter was not found in the database.");
        }
    }
    
    @Test
    void saveWithDxCodes() {
        // new encounter
        Encounter newEncounter = buildEncounter();
        newEncounter.addDiagnosis(DiagnosisCode.from("A36.3", "Cutaneous diphtheria"));
        // save encounter
        Encounter savedEncounter = encounterDao.save(newEncounter);
        // re-load encounter from database
        Optional<Encounter> result = encounterDao.findById(savedEncounter.getId());
        if (result.isPresent()) {
            // assert results
            Encounter retrievedEncounter = result.get();
            assertEquals(1, retrievedEncounter.getDiagnosisCodes().size());
            DiagnosisCode diagnosisCode = retrievedEncounter.getDiagnosisCodes().iterator().next();
            assertEquals("A36.3", diagnosisCode.getIcdCode());
            assertEquals("Cutaneous diphtheria", diagnosisCode.getDescription());
        } else {
            fail("Encounter was not found in the database");
        }
    }
    
    @Test
    void saveWithMultipleDxCodesAndMultipleProcedureCodes() {
        // new encounter
        Encounter newEncounter = buildEncounter();
        newEncounter.addProcedure(ProcedureCode.from("86930", "Frozen blood prep"));
        newEncounter.addProcedure(ProcedureCode.from("86950", "Leukacyte transfusion"));
        newEncounter.addDiagnosis(DiagnosisCode.from("A36.3", "Cutaneous diphtheria"));
        newEncounter.addDiagnosis(DiagnosisCode.from("A36.0", "Pharyngeal diphtheria"));
        // save encounter
        Encounter savedEncounter = encounterDao.save(newEncounter);
        // re-load encounter from database
        Optional<Encounter> result = encounterDao.findById(savedEncounter.getId());
        if (result.isPresent()) {
            // assert results
            Encounter retrievedEncounter = result.get();
            assertEquals(2, retrievedEncounter.getProcedureCodes().size());
            assertEquals(2, retrievedEncounter.getDiagnosisCodes().size());
        } else {
            fail("Encounter was not found in the database");
        }
    }
    
    private Encounter buildEncounter() {
        Encounter encounter = new Encounter();
        encounter.setNotes("I like monkeys!");
        encounter.setStatus(EncounterStatus.NEW);
        encounter.setPatientId(UUID.randomUUID().toString());
        return encounter;
    }
}
