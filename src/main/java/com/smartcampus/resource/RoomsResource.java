package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.store.SmartCampusStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/rooms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoomsResource {
    private final SmartCampusStore store = SmartCampusStore.getInstance();

    @GET
    public List<Room> list() {
        return store.listRooms();
    }

    @POST
    public Response create(Room request, @Context UriInfo uriInfo) {
        Room created = store.createRoom(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Room get(@PathParam("id") String id) {
        return store.getRoom(id);
    }

    @PUT
    @Path("/{id}")
    public Room update(@PathParam("id") String id, Room request) {
        return store.updateRoom(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        store.deleteRoom(id);
        return Response.noContent().build();
    }
}
