package com.svhelloworld.cdc.encounters;

import com.svhelloworld.cdc.encounters.model.DiagnosisCode;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import com.svhelloworld.cdc.encounters.model.ProcedureCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Testing the equality and hashcode methods of the {@link com.svhelloworld.cdc.encounters.model.Encounter}.
 */
public class EncounterEqualityTest {
    
    private static final Instant NOW = Instant.now();
    
    private Encounter thisEncounter;
    private Encounter thatEncounter;
    
    @BeforeEach
    public void setupEncounters() {
        thisEncounter = buildEncounter();
        thatEncounter = buildEncounter();
    }
    
    @Test
    public void equals() {
        assertEquality();
    }
    
    @Test
    public void changeNotes() {
        thatEncounter.setNotes("new note");
        assertInequality();
    }
    
    @Test
    public void changeStatus() {
        thatEncounter.setStatus(EncounterStatus.IN_PROGRESS);
        assertInequality();
    }
    
    @Test
    public void changePatientId() {
        thatEncounter.setPatientId("NEW-PATIENT-ID");
        assertInequality();
    }
    
    @Test
    public void addProcedure() {
        thatEncounter.addProcedure(ProcedureCode.from("BLUE", "GREEN"));
        assertInequality();
    }
    
    @Test
    public void addDiagnosis() {
        thatEncounter.addDiagnosis(DiagnosisCode.from("BLUE", "GREEN"));
        assertInequality();
    }
    
    @Test
    public void addRemoveProcedureCode() {
        ProcedureCode code = ProcedureCode.from("BLUE", "GREEN");
        thatEncounter.addProcedure(code);
        assertInequality();
        thatEncounter.getProcedureCodes().remove(code);
        assertEquality();
    }
    
    @Test
    public void addRemoveDiagnosisCode() {
        DiagnosisCode code = DiagnosisCode.from("GREEN", "BLUE");
        thatEncounter.addDiagnosis(code);
        assertInequality();
        thatEncounter.getDiagnosisCodes().remove(code);
        assertEquality();
    }
    
    private void assertEquality() {
        assertEquals(thisEncounter, thatEncounter);
        assertEquals(thisEncounter.hashCode(), thatEncounter.hashCode());
    }
    
    private void assertInequality() {
        assertNotEquals(thisEncounter, thatEncounter);
        assertNotEquals(thisEncounter, thatEncounter);
    }
    
    private Encounter buildEncounter() {
        Encounter encounter = new Encounter();
        
        encounter.setCreatedOn(NOW);
        encounter.setId(12345);
        encounter.setNotes("foo bar");
        encounter.setStatus(EncounterStatus.NEW);
        encounter.setPatientId("1234-ABCD-5678-EFGH");
        
        encounter.addProcedure(ProcedureCode.from("CPT-CODE1", "Description"));
        encounter.addProcedure(ProcedureCode.from("CPT-CODE2", "Description"));
        encounter.addProcedure(ProcedureCode.from("CPT-CODE3", "Description"));
        
        encounter.addDiagnosis(DiagnosisCode.from("ICD-CODE1", "Description"));
        encounter.addDiagnosis(DiagnosisCode.from("ICD-CODE2", "Description"));
        encounter.addDiagnosis(DiagnosisCode.from("ICD-CODE3", "Description"));
        
        return encounter;
    }
    
}
