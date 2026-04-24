package com.smartcampus.mappers;

import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// Maps SensorUnavailableException to HTTP 403 Forbidden
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    private static final Logger LOGGER = Logger.getLogger(SensorUnavailableExceptionMapper.class.getName());

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        LOGGER.warning("Sensor Unavailable: " + exception.getMessage());

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", exception.getMessage());

        // 403 Forbidden
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseBody)
                .build();
    }
}