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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Glue code to execute steps defined in the Cucumber feature files.
 */
public class EncounterNotificationSteps {
    
    private static final Logger log = LoggerFactory.getLogger(EncounterNotificationSteps.class);
    /**
     * How long will we wait for an event to show up after changing data (in milliseconds).
     */
    private static final int EVENT_WAIT_TIME = 5000;
    
    private final EncounterService encounterService;
    private final EncounterEventsConsumer eventsConsumer;
    /**
     * The entity that the scenarios perform CRUD operations against.
     */
    private Encounter targetEncounter;
    /**
     * The notification event after the encounter is saved
     */
    private Event<Encounter> event;
    
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
        assertAllOutboxEntriesAreResolved();
        eventsConsumer.clearEvents();
    }
    
    @After
    public void teardownScenario(Scenario scenario) {
        assertAllOutboxEntriesAreResolved();
        log.info("Test scenario: [{}] {}", scenario.getName(), scenario.getStatus());
    }
    
    /*
     * GIVEN step definitions
     */
    
    @Given("a new encounter that has not been saved")
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
    public void theEncounterIsSaved() throws InterruptedException {
        log.debug("Saving the target encounter");
        targetEncounter = encounterService.saveEncounter(targetEncounter);
        event = fetchNotificationEvent();
    }
    
    /*
     * THEN step definitions
     */
    
    @Then("I am notified that a new encounter has been created")
    public void iAmNotifiedThatANewEncounterHasBeenCreated() {
        assertNotNull(event, "Did not receive a notification event.");
    }
    
    @Then("I am notified that an existing encounter has been updated")
    public void iAmNotifiedThatAnExistingEncounterHasBeenUpdated() {
        this.iAmNotifiedThatANewEncounterHasBeenCreated();
    }
    
    @Then("the notification contains a matching copy of the encounter")
    public void theNotificationContainsAnExactCopyOfTheEncounter() {
        assertEquals(targetEncounter, event.getBody());
    }
    
    @Then("the encounter in the notification contains the diagnosis code [{diagnosisCode}]")
    public void theEncounterInTheNotificationContainsTheDiagnosisCodeA(DiagnosisCode diagnosisCode) {
        assertTrue(event.getBody().getDiagnosisCodes().contains(diagnosisCode));
    }
    
    @Then("the encounter in the notification contains the procedure code [{procedureCode}]")
    public void theEncounterInTheNotificationContainsTheProcedureCode(ProcedureCode procedureCode) {
        assertTrue(event.getBody().getProcedureCodes().contains(procedureCode));
    }
    
    @Then("the encounter in the notification does not contain the diagnosis code [{diagnosisCode}]")
    public void theEncounterInTheNotificationDoesNotContainTheDiagnosisCodeA(DiagnosisCode diagnosisCode) {
        assertFalse(event.getBody().getDiagnosisCodes().contains(diagnosisCode));
    }
    
    @Then("the encounter in the notification does not contain the procedure code [{procedureCode}]")
    public void theEncounterInTheNotificationDoesNotContainTheProcedureCode(ProcedureCode procedureCode) {
        assertFalse(event.getBody().getProcedureCodes().contains(procedureCode));
    }
    
    @Then("the notes of the encounter in the notification is {string}")
    public void theNotesOfTheEncounterInTheNotificationIs(String notes) {
        assertEquals(notes, event.getBody().getNotes());
    }
    
    @Then("the status of the encounter in the notification is [{encounterStatus}]")
    public void theStatusOfTheEncounterInTheNotificationIs(EncounterStatus encounterStatus) {
        assertEquals(encounterStatus, event.getBody().getStatus());
    }
    
    /**
     * Wait for a specified amount of time for an event to show up from the event bus. Will return once an event is
     * received or the specified EVENT_WAIT_TIME has passed.
     * @throws IllegalArgumentException when no event is caught
     */
    private Event<Encounter> fetchNotificationEvent() throws InterruptedException {
        event = null;
        Instant start = Instant.now();
        int startingEventCount = eventsConsumer.numberEventsReceived();
        // wait until an event shows up or the max wait time
        while (Duration.between(start, Instant.now()).toMillis() < EVENT_WAIT_TIME &&
                eventsConsumer.numberEventsReceived() == startingEventCount) {
            TimeUnit.MILLISECONDS.sleep(200);
        }
        // if an event showed up, return it
        if (startingEventCount < eventsConsumer.numberEventsReceived()) {
            return eventsConsumer.mostRecentEvent().orElseThrow(IllegalArgumentException::new);
        } else {
            throw new IllegalArgumentException("Event was never received.");
        }
    }
    
    private void assertAllOutboxEntriesAreResolved() {
        List<EncounterOutboxEntry> entries = encounterService.getUnresolvedOutboxEntries();
        assertTrue(entries.isEmpty(),
                "Expected no UNRESOLVED encounter outbox entries but found " + entries.size() +
                        "  UNRESOLVED outbox entries");
    }
}
