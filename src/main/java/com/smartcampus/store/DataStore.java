package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory database simulator using thread-safe collections.
 */
public class DataStore {
    private static DataStore instance;
    
    // Thread-safe maps to store our data
    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    
    // Nested map for readings: SensorID -> List of Readings
    private Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {}

    // Singleton pattern
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // --- Room Operations ---
    public Map<String, Room> getRooms() { return rooms; }
    public Room getRoom(String id) { return rooms.get(id); }
    public void putRoom(Room room) { rooms.put(room.getId(), room); }
    public void deleteRoom(String id) { rooms.remove(id); }

    // --- Sensor Operations ---
    public Map<String, Sensor> getSensors() { return sensors; }
    public Sensor getSensor(String id) { return sensors.get(id); }
    public void putSensor(Sensor sensor) { sensors.put(sensor.getId(), sensor); }
    public void deleteSensor(String id) { sensors.remove(id); }

    // --- Reading Operations ---
    public List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }
    
    public void addReading(String sensorId, SensorReading reading) {
        // Create a new list for this sensor if it doesn't exist yet
        readings.putIfAbsent(sensorId, new ArrayList<>());
        // Add the reading to the list
        readings.get(sensorId).add(reading);
    }
}