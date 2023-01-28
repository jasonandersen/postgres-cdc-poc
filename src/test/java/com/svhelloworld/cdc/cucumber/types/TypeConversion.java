package com.svhelloworld.cdc.cucumber.types;

import com.svhelloworld.cdc.encounters.model.DiagnosisCode;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import com.svhelloworld.cdc.encounters.model.ProcedureCode;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.util.Map;

/**
 * This class gives us a place to consolidate Cucumber test parameter type conversions. This allows us to pass in
 * fully populated POJOs into step definition methods so the conversion doesn't have to be done within the test itself.
 */
public class TypeConversion {
    
    private final DataTableTransformer dataTableTransformer;
    
    public TypeConversion(DataTableTransformer dataTableTransformer) {
        this.dataTableTransformer = dataTableTransformer;
    }
    
    /**
     * Convert Cucumber tables in feature files to {@link Encounter} objects.
     */
    @DataTableType
    public Encounter encounter(Map<String, String> input) {
        return dataTableTransformer.transformToBean(input, Encounter.class);
    }
    
    @DataTableType
    public ProcedureCode procedureCode(Map<String, String> input) {
        return dataTableTransformer.transformToBean(input, ProcedureCode.class);
    }
    
    @DataTableType
    public DiagnosisCode diagnosisCode(Map<String, String> input) {
        return dataTableTransformer.transformToBean(input, DiagnosisCode.class);
    }
    
    @ParameterType(".*")
    public EncounterStatus encounterStatus(String input) {
        return EncounterStatus.valueOf(input);
    }
    
    @ParameterType(".*")
    public ProcedureCode procedureCode(String input) {
        ProcedureCode out = new ProcedureCode();
        out.setCptCode(input);
        out.setDescription("Test Procedure Code");
        return out;
    }
    
    @ParameterType(".*")
    public DiagnosisCode diagnosisCode(String input) {
        DiagnosisCode out = new DiagnosisCode();
        out.setIcdCode(input);
        out.setDescription("Test Diagnosis Code");
        return out;
    }
    
}
