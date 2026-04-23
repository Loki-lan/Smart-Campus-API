package com.smartcampus.mapping;

import com.smartcampus.error.ApiError;
import com.smartcampus.error.SmartCampusConflictException;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<SmartCampusConflictException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(SmartCampusConflictException exception) {
        ApiError error = new ApiError(
                Response.Status.CONFLICT.getStatusCode(),
                Response.Status.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                uriInfo == null ? null : uriInfo.getPath(),
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}
