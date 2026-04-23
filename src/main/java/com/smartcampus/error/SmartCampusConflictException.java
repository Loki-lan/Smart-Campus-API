package com.smartcampus.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class SmartCampusConflictException extends WebApplicationException {
    public SmartCampusConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
