package com.smartcampus.mapping;

import com.smartcampus.error.ApiError;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ForbiddenException exception) {
        ApiError error = new ApiError(
                Response.Status.FORBIDDEN.getStatusCode(),
                Response.Status.FORBIDDEN.getReasonPhrase(),
                exception.getMessage(),
                uriInfo == null ? null : uriInfo.getPath(),
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}
