package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.store.SmartCampusStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/sensors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorsResource {
    private final SmartCampusStore store = SmartCampusStore.getInstance();

    @GET
    public List<Sensor> list(@QueryParam("type") String type) {
        return store.listSensors(Optional.ofNullable(type).filter(s -> !s.isBlank()));
    }

    @POST
    public Response create(Sensor request, @Context UriInfo uriInfo) {
        Sensor created = store.createSensor(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Sensor get(@PathParam("id") String id) {
        return store.getSensor(id);
    }

    @PUT
    @Path("/{id}")
    public Sensor update(@PathParam("id") String id, Sensor request) {
        return store.updateSensor(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        store.deleteSensor(id);
        return Response.noContent().build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingsResource readings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingsResource(sensorId);
    }
}
