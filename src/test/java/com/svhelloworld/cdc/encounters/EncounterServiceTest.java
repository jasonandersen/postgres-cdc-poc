package com.svhelloworld.cdc.encounters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncounterServiceTest {
    @Autowired
    private EncounterService service;
    
    @Test
    void validateScheduledAnnotationWorks() throws InterruptedException {
        Thread.sleep(5000);
    }
}
