package com.smartcampus.store;

import com.smartcampus.error.SmartCampusConflictException;
import com.smartcampus.error.UnprocessableEntityException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.model.SensorStatus;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SmartCampusStore {
    private static final SmartCampusStore INSTANCE = new SmartCampusStore();

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readingsBySensorId = new ConcurrentHashMap<>();

    private final AtomicLong roomIdSequence = new AtomicLong(0);
    private final AtomicLong sensorIdSequence = new AtomicLong(0);
    private final AtomicLong readingIdSequence = new AtomicLong(0);

    private SmartCampusStore() {
    }

    public static SmartCampusStore getInstance() {
        return INSTANCE;
    }

    public List<Room> listRooms() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getId))
                .collect(Collectors.toList());
    }

    public Room getRoom(String id) {
        Room room = rooms.get(id);
        if (room == null) {
            throw new NotFoundException();
        }
        return room;
    }

    public synchronized Room createRoom(Room request) {
        Objects.requireNonNull(request, "request");
        String id = request.getId();
        if (id == null || id.isBlank()) {
            id = String.valueOf(roomIdSequence.incrementAndGet());
        }

        Room room = new Room();
        room.setId(id);
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        room.setSensorIds(List.of());

        rooms.put(id, room);
        return room;
    }

    public synchronized Room updateRoom(String id, Room request) {
        Objects.requireNonNull(request, "request");
        Room existing = getRoom(id);

        existing.setName(request.getName());
        existing.setCapacity(request.getCapacity());
        return existing;
    }

    public synchronized void deleteRoom(String id) {
        Room existing = getRoom(id);
        boolean hasActiveSensors = sensors.values().stream()
                .anyMatch(s -> Objects.equals(s.getRoomId(), id) && s.getStatus() == SensorStatus.ACTIVE);

        if (hasActiveSensors) {
            throw new SmartCampusConflictException("Room has active sensors and cannot be deleted");
        }

        sensors.values().stream()
                .filter(s -> Objects.equals(s.getRoomId(), id))
                .forEach(s -> s.setRoomId(null));

        rooms.remove(existing.getId());
    }

    public List<Sensor> listSensors(Optional<String> typeFilter) {
        return sensors.values().stream()
                .filter(s -> typeFilter.isEmpty() || (s.getType() != null && s.getType().equalsIgnoreCase(typeFilter.get())))
                .sorted(Comparator.comparing(Sensor::getId))
                .collect(Collectors.toList());
    }

    public Sensor getSensor(String id) {
        Sensor sensor = sensors.get(id);
        if (sensor == null) {
            throw new NotFoundException();
        }
        return sensor;
    }

    public synchronized Sensor createSensor(Sensor request) {
        Objects.requireNonNull(request, "request");
        if (request.getRoomId() != null && !rooms.containsKey(request.getRoomId())) {
            throw new UnprocessableEntityException("roomId does not exist");
        }

        String id = request.getId();
        if (id == null || id.isBlank()) {
            id = String.valueOf(sensorIdSequence.incrementAndGet());
        }

        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setType(request.getType());
        sensor.setStatus(request.getStatus() == null ? SensorStatus.INACTIVE : request.getStatus());
        sensor.setCurrentValue(request.getCurrentValue());
        sensor.setRoomId(request.getRoomId());

        sensors.put(id, sensor);
        readingsBySensorId.put(id, Collections.synchronizedList(new ArrayList<>()));

        if (sensor.getRoomId() != null) {
            getRoom(sensor.getRoomId()).addSensorId(id);
        }
        return sensor;
    }

    public synchronized Sensor updateSensor(String id, Sensor request) {
        Objects.requireNonNull(request, "request");
        Sensor existing = getSensor(id);
        if (existing.getStatus() == SensorStatus.MAINTENANCE) {
            throw new ForbiddenException("Sensor is in MAINTENANCE");
        }
        if (request.getRoomId() != null && !rooms.containsKey(request.getRoomId())) {
            throw new UnprocessableEntityException("roomId does not exist");
        }

        String oldRoomId = existing.getRoomId();
        String newRoomId = request.getRoomId();
        if (!Objects.equals(oldRoomId, newRoomId)) {
            if (oldRoomId != null && rooms.containsKey(oldRoomId)) {
                getRoom(oldRoomId).removeSensorId(existing.getId());
            }
            if (newRoomId != null) {
                getRoom(newRoomId).addSensorId(existing.getId());
            }
            existing.setRoomId(newRoomId);
        }

        existing.setType(request.getType());
        existing.setStatus(request.getStatus() == null ? existing.getStatus() : request.getStatus());
        return existing;
    }

    public synchronized void deleteSensor(String id) {
        Sensor existing = getSensor(id);
        if (existing.getStatus() == SensorStatus.MAINTENANCE) {
            throw new ForbiddenException("Sensor is in MAINTENANCE");
        }

        if (existing.getRoomId() != null && rooms.containsKey(existing.getRoomId())) {
            getRoom(existing.getRoomId()).removeSensorId(existing.getId());
        }

        sensors.remove(existing.getId());
        readingsBySensorId.remove(existing.getId());
    }

    public List<SensorReading> listReadings(String sensorId) {
        getSensor(sensorId);
        return new ArrayList<>(readingsBySensorId.getOrDefault(sensorId, List.of()));
    }

    public synchronized SensorReading addReading(String sensorId, SensorReading request) {
        Objects.requireNonNull(request, "request");
        Sensor sensor = getSensor(sensorId);
        if (sensor.getStatus() == SensorStatus.MAINTENANCE) {
            throw new ForbiddenException("Sensor is in MAINTENANCE");
        }

        String readingId = request.getId();
        if (readingId == null || readingId.isBlank()) {
            readingId = String.valueOf(readingIdSequence.incrementAndGet());
        }

        SensorReading reading = new SensorReading();
        reading.setId(readingId);
        reading.setTimestamp(request.getTimestamp() == 0 ? System.currentTimeMillis() : request.getTimestamp());
        reading.setValue(request.getValue());

        readingsBySensorId.computeIfAbsent(sensorId, ignored -> Collections.synchronizedList(new ArrayList<>())).add(reading);
        sensor.setCurrentValue(reading.getValue());
        return reading;
    }
}
