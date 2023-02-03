package com.svhelloworld.cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("com.svhelloworld.cdc")
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    
    public Config() {
        log.info("Spring Java configuration instantiated.");
    }
}
