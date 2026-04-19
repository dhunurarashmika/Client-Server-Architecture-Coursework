package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part 3 - Sensor Operations & Linking
 * Handles API requests for /api/v1/sensors
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    // POST /api/v1/sensors - Register a new sensor
    @POST
    public Response createSensor(Sensor sensor) {
        // Basic validation for Sensor ID
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Sensor ID is required.\"}")
                    .build();
        }

        // Integrity Check: Verify that the roomId specified actually exists in the system
        Room parentRoom = store.getRoom(sensor.getRoomId());
        if (parentRoom == null) {
            // Throws exception if room doesn't exist (Mapped to 422 in Part 5)
            throw new LinkedResourceNotFoundException("Cannot create sensor: Room ID '" + sensor.getRoomId() + "' does not exist.");
        }

        // Save the sensor to the database
        store.putSensor(sensor);

        // Link the sensor to the room (Update the room's sensorIds list)
        if (parentRoom.getSensorIds() != null) {
            parentRoom.getSensorIds().add(sensor.getId());
        }

        // Return 201 Created with the new sensor object
        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId()))
                .entity(sensor)
                .build();
    }

    // GET /api/v1/sensors?type={type} - Get sensors, with optional filtering
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(store.getSensors().values());

        // If a 'type' query parameter is provided, filter the list
        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filteredSensors = allSensors.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
            return Response.ok(filteredSensors).build();
        }

        // If no filter is provided, return all sensors
        return Response.ok(allSensors).build();
    }
    
    /**
     * Sub-Resource Locator: Delegates requests for {sensorId}/readings 
     * to the SensorReadingResource class.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        // We pass the sensorId to the constructor of the sub-resource
        return new SensorReadingResource(sensorId);
    }
}