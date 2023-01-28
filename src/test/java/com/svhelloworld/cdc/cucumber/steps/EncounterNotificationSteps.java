package com.svhelloworld.cdc.cucumber.steps;

import com.svhelloworld.cdc.Event;
import com.svhelloworld.cdc.encounters.EncounterEventsConsumer;
import com.svhelloworld.cdc.encounters.EncounterService;
import com.svhelloworld.cdc.encounters.model.DiagnosisCode;
import com.svhelloworld.cdc.encounters.model.Encounter;
import com.svhelloworld.cdc.encounters.model.EncounterOutboxEntry;
import com.svhelloworld.cdc.encounters.model.EncounterStatus;
import com.svhelloworld.cdc.encounters.model.ProcedureCode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Glue code to execute steps defined in the Cucumber feature files.
 */
public class EncounterNotificationSteps {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterNotificationSteps.class);
    private static final int EVENT_WAIT_TIME = 10000;
    
    private final EncounterService encounterService;
    private final EncounterEventsConsumer eventsConsumer;
    /**
     * The entity that the scenarios perform CRUD operations against.
     */
    private Encounter targetEncounter;
    /**
     * The entity that comes back from the event notification of CRUD events.
     */
    private Encounter encounterFromEvent;
    
    /**
     * Cucumber step definition classes are Spring managed beans with their own special life-cycle scope. All
     * dependencies are injected via Spring's application context.
     */
    public EncounterNotificationSteps(
            EncounterService encounterService,
            EncounterEventsConsumer eventsConsumer) {
        
        this.encounterService = encounterService;
        this.eventsConsumer = eventsConsumer;
        log.debug("Change data capture step definitions instantiated.");
    }
    
    @Before
    public void setupScenario(Scenario scenario) {
        log.info("Test scenario: [{}] START", scenario.getName());
        
        // assert that we have no outstanding outbox entries before we start the test
        assertAllOutboxEntriesAreResolved();
        
        // make sure no events carry over from previous test scenarios
        log.debug("Clearing {} events prior to executing test scenario", eventsConsumer.numberEventsReceived());
        eventsConsumer.clearEvents();
    }
    
    @After
    public void teardownScenario(Scenario scenario) {
        // assert that we have no outstanding outbox entries that will carry over into the next test
        assertAllOutboxEntriesAreResolved();
        
        log.info("Test scenario: [{}] {}", scenario.getName(), scenario.getStatus());
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
    
    @Given("the encounter status is set to [{encounterStatus}]")
    public void theEncounterStatusIsChangedTo(EncounterStatus status) {
        targetEncounter.setStatus(status);
    }
    
    @Given("the encounter notes is updated to {string}")
    public void theEncounterNotesIsUpdatedTo(String notes) {
        targetEncounter.setNotes(notes);
    }
    
    @Given("the diagnosis code [{diagnosisCode}] is added to the encounter")
    public void theDiagnosisCodeIsAddedToTheEncounter(DiagnosisCode diagnosisCode) {
        targetEncounter.addDiagnosis(diagnosisCode);
    }
    
    @Given("the procedure code [{procedureCode}] is added to the encounter")
    public void theProcedureCodeIsAddedToTheEncounter(ProcedureCode procedureCode) {
        targetEncounter.addProcedure(procedureCode);
    }
    
    @Given("the diagnosis code [{diagnosisCode}] is removed from the encounter")
    public void theDiagnosisCodeAIsRemovedFromTheEncounter(DiagnosisCode diagnosisCode) {
        targetEncounter.removeDiagnosis(diagnosisCode);
    }
    
    @Given("the procedure code [{procedureCode}] is removed from the encounter")
    public void theProcedureCodeIsRemovedFromTheEncounter(ProcedureCode procedureCode) {
        targetEncounter.removeProcedure(procedureCode);
    }
    
    /*
     * WHEN step definitions
     */
    
    @When("the encounter is saved( again)")
    public void theEncounterIsSaved() {
        log.debug("Saving the target encounter");
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
        encounterFromEvent = mostRecentEventPayload();
    }
    
    @Then("I am notified that an existing encounter has been updated")
    public void iAmNotifiedThatAnExistingEncounterHasBeenUpdated() throws InterruptedException {
        this.iAmNotifiedThatANewEncounterHasBeenCreated();
    }
    
    @Then("the notification contains an exact copy of the encounter")
    public void theNotificationContainsAnExactCopyOfTheEncounter() {
        assertEquals(targetEncounter, encounterFromEvent);
    }
    
    @Then("the encounter in the notification contains the diagnosis code [{diagnosisCode}]")
    public void theEncounterInTheNotificationContainsTheDiagnosisCodeA(DiagnosisCode diagnosisCode) {
        assertTrue(encounterFromEvent.getDiagnosisCodes().contains(diagnosisCode));
    }
    
    @Then("the encounter in the notification contains the procedure code [{procedureCode}]")
    public void theEncounterInTheNotificationContainsTheProcedureCode(ProcedureCode procedureCode) {
        assertTrue(encounterFromEvent.getProcedureCodes().contains(procedureCode));
    }
    
    @Then("the encounter in the notification does not contain the diagnosis code [{diagnosisCode}]")
    public void theEncounterInTheNotificationDoesNotContainTheDiagnosisCodeA(DiagnosisCode diagnosisCode) {
        assertFalse(encounterFromEvent.getDiagnosisCodes().contains(diagnosisCode));
    }
    
    @Then("the encounter in the notification does not contain the procedure code [{procedureCode}]")
    public void theEncounterInTheNotificationDoesNotContainTheProcedureCode(ProcedureCode procedureCode) {
        assertFalse(encounterFromEvent.getProcedureCodes().contains(procedureCode));
    }
    
    @Then("the notes of the encounter in the notification is {string}")
    public void theNotesOfTheEncounterInTheNotificationIs(String notes) {
        assertEquals(notes, encounterFromEvent.getNotes());
    }
    
    @Then("the status of the encounter in the notification is [{encounterStatus}]")
    public void theStatusOfTheEncounterInTheNotificationIs(EncounterStatus encounterStatus) {
        assertEquals(encounterStatus, encounterFromEvent.getStatus());
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
        Optional<Event<Encounter>> event = eventsConsumer.mostRecentEvent();
        return (Encounter) event
                .orElseThrow(IllegalArgumentException::new)
                .getBody();
    }
    
    private void assertAllOutboxEntriesAreResolved() {
        List<EncounterOutboxEntry> entries = encounterService.getUnresolvedOutboxEntries();
        assertTrue(entries.isEmpty(),
                "Expected no UNRESOLVED encounter outbox entries but found " + entries.size() +
                        "  UNRESOLVED outbox entries");
    }
}
