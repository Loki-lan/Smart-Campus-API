package com.smartcampus.mapping;

import com.smartcampus.error.ApiError;
import com.smartcampus.error.SmartCampusConflictException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
