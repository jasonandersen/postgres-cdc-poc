Feature: When an encounter is created or modified in any we, we are notified of the changes

  Background:
    Given this new encounter that has not been saved
      | PatientId                            | StatusId | Notes                |
      | debbcd5e-98d4-11ed-a8fc-0242ac120002 | 100      | Here are some notes. |
    And the encounter has these CPT codes
      | 86931 |
      | 86960 |
      | 86985 |

  Scenario: Create a new encounter
    When the encounter is saved
    Then I am notified that a new encounter has been created

  # Update existing encounter

  # Delete encounter

  # Add DX code to encounter

  # Add CPT code to encounter

  # Remove CPT code from encounter

  # Remove DX code from encounter

  # Add multiple CPT codes and DX codes from encounter