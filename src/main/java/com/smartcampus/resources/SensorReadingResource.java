package com.smartcampus.resources;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.storage.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

public class SensorReadingResource {
    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        return store.getReadings(sensorId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor s = store.getSensor(sensorId);
        if (s == null)
            return Response.status(404).build();
        if ("MAINTENANCE".equals(s.getStatus()))
            throw new SensorUnavailableException("Sensor down!");

        store.getReadings(sensorId).add(reading);
        s.setCurrentValue(reading.getValue());
        return Response.status(201).entity(reading).build();
    }
}