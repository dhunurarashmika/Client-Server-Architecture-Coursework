package com.smartcampus.resources;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.storage.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();

    @GET
    public List<Room> getAll() {
        return new ArrayList<>(store.getRooms().values());
    }

    @POST
    public Response create(Room room) {
        store.putRoom(room);
        return Response.status(201).entity(room).build();
    }

    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room r = store.getRoom(id);
        if (r == null) {
            return Response.status(404).build();
        }
        return Response.ok(r).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        Room r = store.getRoom(id);
        if (r == null)
            return Response.status(404).build();
        if (!r.getSensorIds().isEmpty())
            throw new RoomNotEmptyException("Room has sensors!");
        store.deleteRoom(id);
        return Response.noContent().build();
    }
}