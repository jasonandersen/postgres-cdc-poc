package com.svhelloworld.cdc.cucumber.types;

import com.svhelloworld.cdc.encounters.model.DiagnosisCode;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import com.svhelloworld.cdc.encounters.model.ProcedureCode;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.ParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UnknownFormatConversionException;

/**
 * This class gives us a place to consolidate Cucumber test parameter type conversions. This allows us to pass in
 * fully populated POJOs into step definition methods so the conversion doesn't have to be done within the test itself.
 */
public class TypeConversion {
    
    private static final Logger log = LoggerFactory.getLogger(TypeConversion.class);
    private static final String PROCEDURE_CODE_TYPE = ProcedureCode.class.getName();
    private static final String DIAGNOSIS_CODE_TYPE = DiagnosisCode.class.getName();
    
    private final InputTransformer inputTransformer;
    
    public TypeConversion(InputTransformer inputTransformer) {
        this.inputTransformer = inputTransformer;
    }
    
    /**
     * Convert Cucumber tables in feature files to {@link Encounter} objects.
     */
    @DataTableType
    public Encounter encounter(Map<String, String> input) {
        return inputTransformer.transformToBean(input, Encounter.class);
    }
    
    @ParameterType(".*")
    public EncounterStatus encounterStatus(String input) {
        return EncounterStatus.valueOf(input);
    }
    
    @ParameterType(".*")
    public ProcedureCode procedureCode(String input) {
        return (ProcedureCode) defaultTransformation(input, ProcedureCode.class);
    }
    
    @ParameterType(".*")
    public DiagnosisCode diagnosisCode(String input) {
        return (DiagnosisCode) defaultTransformation(input, DiagnosisCode.class);
    }
    
    /**
     * Convert simple input parameters from tables.
     */
    @DefaultDataTableCellTransformer
    public Object defaultTransformation(String input, Type type) {
        
        log.info("CPT code: {}, Type: {}", input, type);
        
        // convert procedure codes
        if (PROCEDURE_CODE_TYPE.equals(type.getTypeName())) {
            ProcedureCode out = new ProcedureCode();
            out.setCptCode(input);
            out.setDescription("Test Procedure Code");
            return out;
        }
        
        // convert diagnosis codes
        if (DIAGNOSIS_CODE_TYPE.equals(type.getTypeName())) {
            DiagnosisCode out = new DiagnosisCode();
            out.setIcdCode(input);
            out.setDescription("Test Diagnosis Code");
            return out;
        }
        
        // unknown type passed in, cannot convert
        throw new UnknownFormatConversionException("Unknown type: " + type.getTypeName());
    }
}
