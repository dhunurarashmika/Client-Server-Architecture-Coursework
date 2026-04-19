package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Part 4 - Sub-Resource Locator Pattern
 * Handles operations for a specific sensor's readings.
 * Note: This class does NOT have a @Path annotation at the class level.
 * It is invoked dynamically by SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final DataStore store = DataStore.getInstance();
    private final String sensorId;

    // The parent SensorResource passes the sensorId to this constructor
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings - Fetch historical data
    @GET
    public Response getReadings() {
        List<SensorReading> readings = store.getReadings(sensorId);
        return Response.ok(readings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings - Append new reading
    @POST
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = store.getSensor(sensorId);
        
        // If the parent sensor doesn't exist, we can't add a reading
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Cannot add reading. Sensor '" + sensorId + "' not found.\"}")
                    .build();
        }

        // Generate ID and Timestamp if not provided by the client
        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Add the reading to the database
        store.addReading(sensorId, reading);

        // Side Effect Requirement: Update the parent sensor's currentValue
        parentSensor.setCurrentValue(reading.getValue());

        // Return 201 Created
        return Response.created(URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId()))
                .entity(reading)
                .build();
    }
}