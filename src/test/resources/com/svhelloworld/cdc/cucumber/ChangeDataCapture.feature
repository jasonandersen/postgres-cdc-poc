Feature: Use change data capture to emit domain events from database changes

  Scenario: Insert a new encounter
    Given a new encounter:
      | Notes                | StatusId |
      | Here are some notes. | 100      |
    When the encounter is saved
    Then the encounter is recorded in the outbox
