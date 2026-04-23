package com.smartcampus.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UnprocessableEntityException extends WebApplicationException {
    public UnprocessableEntityException(String message) {
        super(message, Response.status(422).build());
    }
}
