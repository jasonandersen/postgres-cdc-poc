package com.svhelloworld.cdc.cucumber.steps;

import com.svhelloworld.cdc.Event;
import com.svhelloworld.cdc.encounters.EncounterEventsConsumer;
import com.svhelloworld.cdc.encounters.EncounterService;
import com.svhelloworld.cdc.encounters.model.DiagnosisCode;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import com.svhelloworld.cdc.encounters.model.ProcedureCode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Glue code to execute steps defined in the Cucumber step definition files.
 */
public class EncounterNotificationSteps {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterNotificationSteps.class);
    private static final int EVENT_WAIT_TIME = 10000;
    
    private final EncounterService encounterService;
    private final EncounterEventsConsumer eventsConsumer;
    private Encounter targetEncounter;
    
    public EncounterNotificationSteps(
            EncounterService encounterService,
            EncounterEventsConsumer eventsConsumer) {
        
        // Note - Cucumber step definition classes are Spring managed beans so Spring will handle
        // injection of dependencies
        this.encounterService = encounterService;
        this.eventsConsumer = eventsConsumer;
        log.info("Change data capture step definitions instantiated.");
    }
    
    /*
     * GIVEN step definitions
     */
    
    @Given("this new encounter that has not been saved")
    public void aNewEncounter(Encounter encounter) {
        targetEncounter = encounter;
    }
    
    @Given("the encounter has these CPT codes(:)")
    public void theEncounterHasTheseCPTCodes(List<ProcedureCode> cptCodes) {
        cptCodes.forEach(c -> targetEncounter.addProcedure(c));
    }
    
    @Given("the encounter has these ICD codes(:)")
    public void theEncounterHasTheseICDCodes(List<DiagnosisCode> icdCodes) {
        icdCodes.forEach(c -> targetEncounter.addDiagnosis(c));
    }
    
    @Given("the encounter status is set to {encounterStatus}")
    public void theEncounterStatusIsChangedTo(EncounterStatus status) {
        targetEncounter.setStatus(status);
    }
    
    @Given("the encounter notes is updated to {string}")
    public void theEncounterNotesIsUpdatedTo(String notes) {
        targetEncounter.setNotes(notes);
    }
    
    @Given("the diagnosis code {diagnosisCode} is added to the encounter")
    public void theDiagnosisCodeIsAddedToTheEncounter(DiagnosisCode diagnosisCode) {
        targetEncounter.addDiagnosis(diagnosisCode);
    }
    
    @Given("the procedure code {procedureCode} is added to the encounter")
    public void theProcedureCodeIsAddedToTheEncounter(ProcedureCode procedureCode) {
        targetEncounter.addProcedure(procedureCode);
    }
    
    @Given("the diagnosis code {diagnosisCode} is removed from the encounter")
    public void theDiagnosisCodeAIsRemovedFromTheEncounter(DiagnosisCode diagnosisCode) {
        targetEncounter.removeDiagnosis(diagnosisCode);
    }
    
    @Given("the procedure code {procedureCode} is removed from the encounter")
    public void theProcedureCodeIsRemovedFromTheEncounter(ProcedureCode procedureCode) {
        targetEncounter.removeProcedure(procedureCode);
    }
    
    /*
     * WHEN step definitions
     */
    
    @When("the encounter is saved( again)")
    public void theEncounterIsSaved() {
        targetEncounter = encounterService.saveEncounter(targetEncounter);
    }
    
    /*
     * THEN step definitions
     */
    
    @Then("I am notified that a new encounter has been created")
    public void iAmNotifiedThatANewEncounterHasBeenCreated() throws InterruptedException {
        int startingEventCount = eventsConsumer.numberEventsReceived();
        waitForEvent();
        assertEquals(startingEventCount + 1, eventsConsumer.numberEventsReceived());
    }
    
    @Then("I am notified that an existing encounter has been updated")
    public void iAmNotifiedThatAnExistingEncounterHasBeenUpdated() throws InterruptedException {
        this.iAmNotifiedThatANewEncounterHasBeenCreated();
    }
    
    @Then("all encounter outbox entries have been resolved")
    public void allEncounterOutboxEntriesHaveBeenResolved() {
        List<EncounterOutboxEntry> entries = encounterService.getUnresolvedOutboxEntries();
        assertTrue(entries.isEmpty());
    }
    
    @Then("the notification contains an exact copy of the encounter")
    public void theNotificationContainsAnExactCopyOfTheEncounter() {
        Encounter receivedEncounter = mostRecentEventPayload();
        assertEquals(targetEncounter, receivedEncounter);
    }
    
    
    /**
     * Wait for a specified amount of time for an event to show up from the event bus. Will return once an event is
     * received or the specified EVENT_WAIT_TIME has passed.
     */
    private void waitForEvent() throws InterruptedException {
        log.debug("Waiting for event. Events received: {}", eventsConsumer.numberEventsReceived());
        long startTime = System.currentTimeMillis();
        int startingEventCount = eventsConsumer.numberEventsReceived();
        while (System.currentTimeMillis() - startTime < EVENT_WAIT_TIME &&
                eventsConsumer.numberEventsReceived() == startingEventCount) {
            Thread.sleep(200);
        }
        log.debug("Finished waiting for event. Events received: {}", eventsConsumer.mostRecentEvent());
    }
    
    /**
     * Fetch the most recent event and grab the encounter out of the event payload
     */
    private Encounter mostRecentEventPayload() {
        Optional<Event> event = eventsConsumer.mostRecentEvent();
        if (event.isPresent()) {
            Event mostRecent = event.get();
            return (Encounter) mostRecent.getBody();
        }
        throw new IllegalArgumentException("Encounter in event was not found.");
    }
}
