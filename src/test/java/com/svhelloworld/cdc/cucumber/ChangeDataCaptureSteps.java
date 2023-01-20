package com.svhelloworld.cdc.cucumber;

import com.svhelloworld.cdc.Encounter;
import com.svhelloworld.cdc.EncounterDao;
import com.svhelloworld.cdc.cucumber.types.InputTransformer;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Glue code to execute steps defined in the Cucumber step definition files.
 */
public class ChangeDataCaptureSteps {
    
    private static final Logger log = LoggerFactory.getLogger(ChangeDataCaptureSteps.class);
    
    private final EncounterDao dao;
    private final InputTransformer inputTransformer;
    private Encounter targetEncounter;
    
    public ChangeDataCaptureSteps(EncounterDao dao, InputTransformer inputTransformer) {
        log.info("Change data capture step definitions instantiated.");
        this.dao = dao;
        this.inputTransformer = inputTransformer;
    }
    
    @Given("a new encounter:")
    public void aNewEncounter(Encounter encounter) {
        targetEncounter = encounter;
    }
    
    @When("the encounter is saved")
    public void theEncounterIsSaved() {
        targetEncounter = dao.save(targetEncounter);
    }
    
    @Then("the encounter is recorded in the outbox")
    public void theEncounterIsRecordedInTheOutbox() {
        fail();
    }
    
    /**
     * Convert Cucumber tables in feature files to {@link Encounter} objects.
     */
    @DataTableType
    public Encounter encounter(Map<String, String> input) {
        return inputTransformer.transformToBean(input, Encounter.class);
    }
}
