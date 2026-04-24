package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class DataStore {
    private static final DataStore INSTANCE = new DataStore();
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<String, List<SensorReading>>();

    private DataStore() {
        seedData();
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void putRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public void deleteRoom(String id) {
        rooms.remove(id);
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public void putSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public List<SensorReading> getReadings(String sensorId) {
        return readings.computeIfAbsent(sensorId, k -> new ArrayList<SensorReading>());
    }

    private void seedData() {
        Room r1 = new Room("room-101", "Main Lab", 50);
        putRoom(r1);
        Sensor s1 = new Sensor("sensor-01", "Temp", "ACTIVE", 22.0, "room-101");
        putSensor(s1);
        r1.addSensorId(s1.getId());
    }
}