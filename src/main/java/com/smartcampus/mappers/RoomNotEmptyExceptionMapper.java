package com.smartcampus.mappers;

import com.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// Maps RoomNotEmptyException to HTTP 409 Conflict
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    private static final Logger LOGGER = Logger.getLogger(RoomNotEmptyExceptionMapper.class.getName());

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        LOGGER.warning("Room Deletion Conflict: " + exception.getMessage());

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", exception.getMessage());

        // 409 Conflict
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseBody)
                .build();
    }
}