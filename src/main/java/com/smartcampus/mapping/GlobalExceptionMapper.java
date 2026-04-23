package com.smartcampus.mapping;

import com.smartcampus.error.ApiError;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.log(Level.SEVERE, "Unhandled error", exception);

        ApiError error = new ApiError(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error",
                uriInfo == null ? null : uriInfo.getPath(),
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}
