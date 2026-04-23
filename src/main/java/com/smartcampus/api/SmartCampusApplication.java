package com.smartcampus.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // Register resources, mappers, and filters by scanning packages
        packages("com.smartcampus.resource", "com.smartcampus.mapping", "com.smartcampus.logging");
        
        // Register Jackson for JSON support
        register(JacksonFeature.class);
    }
}
