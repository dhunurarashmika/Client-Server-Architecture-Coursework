package com.smartcampus.resources;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// Manages Sensor entities at /api/v1/sensors
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    // POST /api/v1/sensors - Register a new sensor
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error("Sensor data and Room ID are required."))
                    .build();
        }

        // Integrity Check - Ensure the room exists
        Room parentRoom = store.getRoom(sensor.getRoomId());
        if (parentRoom == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor: Room ID '" + sensor.getRoomId() + "' does not exist.");
        }

        // Auto-generate ID if not provided by the client
        if (sensor.getId() == null || sensor.getId().isBlank()) {
            // Corrected to setId (setter method)
            sensor.setId("sensor-" + UUID.randomUUID().toString().substring(0, 8));
        }

        // Set default status if missing
        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) {
            sensor.setStatus("ACTIVE");
        }

        store.putSensor(sensor);

        // Link the sensor to the parent room
        if (parentRoom.getSensorIds() != null) {
            parentRoom.addSensorId(sensor.getId());
        }

        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId()))
                .entity(sensor)
                .build();
    }

    // GET /api/v1/sensors?type={type} - Get sensors with optional filter
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(store.getSensors().values());

        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filteredSensors = allSensors.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
            return Response.ok(filteredSensors).build();
        }

        return Response.ok(allSensors).build();
    }

    // GET /api/v1/sensors/{id} - Get a specific sensor
    @GET
    @Path("/{id}")
    public Response getSensorById(@PathParam("id") String id) {
        Sensor sensor = store.getSensor(id);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Sensor not found: " + id))
                    .build();
        }
        return Response.ok(sensor).build();
    }

    // Sub-Resource Locator: Delegates to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }
}