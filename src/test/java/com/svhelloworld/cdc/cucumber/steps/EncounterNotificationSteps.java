package com.svhelloworld.cdc.cucumber.steps;

import com.svhelloworld.cdc.Encounter;
import com.svhelloworld.cdc.EncounterDao;
import com.svhelloworld.cdc.ProcedureCode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Glue code to execute steps defined in the Cucumber step definition files.
 */
public class EncounterNotificationSteps {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterNotificationSteps.class);
    
    private final EncounterDao dao;
    private Encounter targetEncounter;
    
    public EncounterNotificationSteps(EncounterDao dao) {
        log.info("Change data capture step definitions instantiated.");
        this.dao = dao;
    }
    
    @Given("this new encounter that has not been saved")
    public void aNewEncounter(Encounter encounter) {
        targetEncounter = encounter;
    }
    
    @Given("the encounter has these CPT codes")
    public void theEncounterHasTheseCPTCodes(List<ProcedureCode> cptCodes) {
        cptCodes.forEach(c -> targetEncounter.addProcedure(c));
    }
    
    @When("the encounter is saved")
    public void theEncounterIsSaved() {
        targetEncounter = dao.save(targetEncounter);
    }
    
    @Then("I am notified that a new encounter has been created")
    public void theEncounterIsRecordedInTheOutbox() {
        fail();
    }
    

}
