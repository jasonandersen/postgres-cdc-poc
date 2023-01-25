# We are validating that when we modify entities in the database, we are then notified of those data changes
Feature: When an encounter is created or modified in any way, we are notified of the changes

  Background:
    Given this new encounter that has not been saved
      | PatientId                            | StatusId | Notes                |
      | debbcd5e-98d4-11ed-a8fc-0242ac120002 | 100      | Here are some notes. |
    And the encounter has these CPT codes
      | 86931 |
      | 86960 |
      | 86985 |
    And the encounter has these ICD codes:
      | A36.3 |
      | A36.1 |


  Scenario: Save a new encounter
    When the encounter is saved
    Then I am notified that a new encounter has been created
    And all encounter outbox entries have been resolved


  Scenario: Update existing encounter with a new status
    Given the encounter is saved
    And the encounter status is changed to "IN_PROGRESS"
    When the encounter is saved
    Then I am notified that an existing encounter has been updated


  # Add DX code to encounter

  # Add CPT code to encounter

  # Remove CPT code from encounter

  # Remove DX code from encounter

  # Add multiple CPT codes and DX codes from encounter

  # Delete encounter
