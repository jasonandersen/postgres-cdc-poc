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
 * Testing the equals() and hashcode() methods of the {@link Encounter}.
 */
public class EncounterEqualityTest {
    
    private static final Instant TIMESTAMP = Instant.now();
    
    private Encounter oneEncounter;
    private Encounter twoEncounter;
    
    @BeforeEach
    public void setupEncounters() {
        oneEncounter = buildEncounter();
        twoEncounter = buildEncounter();
        assertEquality();
    }
    
    @Test
    public void changeNotes() {
        twoEncounter.setNotes("new note");
        assertInequality();
    }
    
    @Test
    public void changeStatus() {
        twoEncounter.setStatus(EncounterStatus.IN_PROGRESS);
        assertInequality();
    }
    
    @Test
    public void changePatientId() {
        twoEncounter.setPatientId("NEW-PATIENT-ID");
        assertInequality();
    }
    
    @Test
    public void addProcedure() {
        twoEncounter.addProcedure(ProcedureCode.from("BLUE", "GREEN"));
        assertInequality();
    }
    
    @Test
    public void addDiagnosis() {
        twoEncounter.addDiagnosis(DiagnosisCode.from("BLUE", "GREEN"));
        assertInequality();
    }
    
    @Test
    public void addThenRemoveProcedureCode() {
        ProcedureCode code = ProcedureCode.from("BLUE", "GREEN");
        twoEncounter.addProcedure(code);
        assertInequality();
        twoEncounter.getProcedureCodes().remove(code);
        assertEquality();
    }
    
    @Test
    public void addThenRemoveDiagnosisCode() {
        DiagnosisCode code = DiagnosisCode.from("GREEN", "BLUE");
        twoEncounter.addDiagnosis(code);
        assertInequality();
        twoEncounter.getDiagnosisCodes().remove(code);
        assertEquality();
    }
    
    private void assertEquality() {
        assertEquals(oneEncounter, twoEncounter);
        assertEquals(oneEncounter.hashCode(), twoEncounter.hashCode());
    }
    
    private void assertInequality() {
        assertNotEquals(oneEncounter, twoEncounter);
        assertNotEquals(oneEncounter.hashCode(), twoEncounter.hashCode());
    }
    
    private Encounter buildEncounter() {
        Encounter encounter = new Encounter();
        
        encounter.setCreatedOn(TIMESTAMP);
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
