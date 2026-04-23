package com.smartcampus.mapping;

import com.smartcampus.error.ApiError;
import com.smartcampus.error.UnprocessableEntityException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(UnprocessableEntityException exception) {
        ApiError error = new ApiError(
                422,
                "Unprocessable Entity",
                exception.getMessage(),
                uriInfo == null ? null : uriInfo.getPath(),
                System.currentTimeMillis()
        );

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}
