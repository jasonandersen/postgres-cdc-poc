package com.svhelloworld.cdc.cucumber.types;

import com.svhelloworld.cdc.Encounter;
import com.svhelloworld.cdc.ProcedureCode;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DefaultDataTableCellTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UnknownFormatConversionException;

/**
 * This class gives us a place to consolidate Cucumber data table conversion methods. Each of these methods will
 * take a map of strings as input and convert that map to a POJO.
 */
public class TypeConversion {
    
    private static final Logger log = LoggerFactory.getLogger(TypeConversion.class);
    
    private final InputTransformer inputTransformer;
    
    public TypeConversion(InputTransformer inputTransformer) {
        this.inputTransformer = inputTransformer;
    }
    
    /**
     * Convert simple input parameters.
     */
    @DefaultDataTableCellTransformer
    public Object defaultTransformation(String input, Type type) {
        log.info("CPT code: {}, Type: {}", input, type);
        if (ProcedureCode.class.getName().equals(type.getTypeName())) {
            ProcedureCode out = new ProcedureCode();
            out.setCptCode(input);
            return out;
        }
        throw new UnknownFormatConversionException("Unknown type: " + type.getTypeName());
    }
    
    /**
     * Convert Cucumber tables in feature files to {@link Encounter} objects.
     */
    @DataTableType
    public Encounter encounter(Map<String, String> input) {
        return inputTransformer.transformToBean(input, Encounter.class);
    }
}
