Feature: Ensure we have Cucumber setup properly
  Scenario: Scenario steps are wired up to step definitions
    Given a precondition step
    When we hit our test trigger
    Then the post-condition step is asserted