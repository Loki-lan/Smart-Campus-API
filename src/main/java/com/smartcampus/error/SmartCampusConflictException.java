package com.smartcampus.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class SmartCampusConflictException extends WebApplicationException {
    public SmartCampusConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
