package com.smartcampus.mappers;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// Maps custom exception to HTTP 422
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    private static final Logger LOGGER = Logger.getLogger(LinkedResourceNotFoundExceptionMapper.class.getName());

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        LOGGER.warning("Validation Failed: " + exception.getMessage());

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", exception.getMessage());

        // Return 422 status
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseBody)
                .build();
    }
}