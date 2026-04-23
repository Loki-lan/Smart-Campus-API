package com.smartcampus.api;

import com.smartcampus.logging.RequestResponseLoggingFilter;
import com.smartcampus.mapping.ConflictExceptionMapper;
import com.smartcampus.mapping.ForbiddenExceptionMapper;
import com.smartcampus.mapping.GlobalExceptionMapper;
import com.smartcampus.mapping.NotFoundExceptionMapper;
import com.smartcampus.mapping.UnprocessableEntityExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        packages("com.smartcampus.resource");
        register(RequestResponseLoggingFilter.class);
        register(ConflictExceptionMapper.class);
        register(UnprocessableEntityExceptionMapper.class);
        register(ForbiddenExceptionMapper.class);
        register(NotFoundExceptionMapper.class);
        register(GlobalExceptionMapper.class);
    }
}
