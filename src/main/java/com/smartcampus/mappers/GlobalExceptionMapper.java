package com.smartcampus.mappers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Catch-all mapper to prevent raw Java stack traces from leaking to the client
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // Allow standard JAX-RS exceptions (like 404 or 405) to pass through normally
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // Log the actual error to the server console for debugging
        LOGGER.log(Level.SEVERE, "Unexpected server error", exception);

        // Return a clean, generic 500 error to the client
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", "An unexpected internal server error occurred.");

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseBody)
                .build();
    }
}