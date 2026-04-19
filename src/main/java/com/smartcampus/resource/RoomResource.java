package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(store.getRooms().values());
        return Response.ok(roomList).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error("Room ID is required."))
                    .build();
        }
        if (store.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(error("Room with ID '" + room.getId() + "' already exists."))
                    .build();
        }
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        store.putRoom(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Room '" + roomId + "' not found."))
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} - Decommissions a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Room '" + roomId + "' not found."))
                    .build();
        }
        
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room '" + roomId + "' cannot be deleted: it still has " +
                    room.getSensorIds().size() + " sensor(s) assigned.");
        }
        
        store.deleteRoom(roomId);
        return Response.noContent().build();
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }
}