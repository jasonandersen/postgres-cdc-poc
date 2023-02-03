package com.svhelloworld.cdc.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CucumberSetupSteps {
    
    private static final Logger log = LoggerFactory.getLogger(CucumberSetupSteps.class);
    
    @Given("a precondition step")
    public void aPreconditionStep() {
        log.info("Given a precondition step");
    }
    
    @When("we hit our test trigger")
    public void weHitOurTestTrigger() {
        log.info("When we hit our test trigger");
    }
    
    @Then("the post-condition step is asserted")
    public void thePostConditionStepIsAsserted() {
        log.info("Then the post-condition step is asserted");
    }
}
