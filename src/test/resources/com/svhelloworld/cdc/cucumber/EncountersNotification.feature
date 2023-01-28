#
# ====================================
# Change Data Capture Proof of Concept
# ====================================
#
# We are attempting to prove or disprove that we can use CDC - more specifically the outbox pattern - to capture
# data mutation events around a complex, aggregate entity like encounters.
#
# The basic data flow for this pattern looks like:
#     * encounter DAO saves encounter entity to database
#     * PLPGSQL trigger on encounters, encounter_procedures, encounter_dx tables fires
#     * trigger inserts a record into encounters_outbox
#     * EncounterService polls encounters_outbox for UNRESOLVED entries
#     * All entries in the outbox are retrieved and transformed into domain events
#     * Domain events are published to the world (in this PoC, we're just hard-coding the event publisher to the
#       event consumer to proxy for something like EventBridge or SNS)
#
@encounter
Feature: When an encounter is created or modified, we are notified of the changes


  Background:
    Given this new encounter that has not been saved
      | Status name | Notes                | Patient Id                           |
      | NEW         | Here are some notes. | debbcd5e-98d4-11ed-a8fc-0242ac120002 |
    And the encounter has these CPT codes
      | CPT code | Description                 |
      | 86931    | Frozen blood thaw           |
      | 86960    | Vol reduction of blood/prod |
      | 86985    | Split blood or products     |
    And the encounter has these ICD codes
      | ICD code | Description               |
      | A36.3    | Cutaneous diphtheria      |
      | A36.1    | Nasopharyngeal diphtheria |


  Scenario: Save a new encounter
    When the encounter is saved
    Then I am notified that a new encounter has been created
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved


  Scenario: Update existing encounter with a new status
    Given the encounter is saved
    And the encounter status is set to IN_PROGRESS
    When the encounter is saved
    Then I am notified that an existing encounter has been updated
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved


  Scenario: Update notes on existing encounter
    Given the encounter is saved
    And the encounter notes is updated to "I changed the notes to this value."
    When the encounter is saved
    Then I am notified that an existing encounter has been updated
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved


  Scenario: Add DX code to encounter
    Given the encounter is saved
    And the diagnosis code A36.0 is added to the encounter
    When the encounter is saved again
    Then I am notified that an existing encounter has been updated
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved


  Scenario: Add CPT code to encounter
    Given the encounter is saved
    And the procedure code 86932 is added to the encounter
    When the encounter is saved again
    Then I am notified that an existing encounter has been updated
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved


  Scenario: Remove DX code from encounter
    Given the encounter is saved
    And the diagnosis code A36.3 is removed from the encounter
    When the encounter is saved again
    Then I am notified that an existing encounter has been updated
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved


  Scenario: Remove CPT code from encounter
    Given the encounter is saved
    And the procedure code 86960 is removed from the encounter
    When the encounter is saved again
    Then I am notified that an existing encounter has been updated
    And the notification contains an exact copy of the encounter
    And all encounter outbox entries have been resolved
