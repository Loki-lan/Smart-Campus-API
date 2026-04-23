package com.smartcampus.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UnprocessableEntityException extends WebApplicationException {
    public UnprocessableEntityException(String message) {
        super(message, Response.status(422).build());
    }
}
