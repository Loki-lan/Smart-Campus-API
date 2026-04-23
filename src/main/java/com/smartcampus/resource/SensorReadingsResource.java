package com.smartcampus.resource;

import com.smartcampus.model.SensorReading;
import com.smartcampus.store.SmartCampusStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingsResource {
    private final SmartCampusStore store = SmartCampusStore.getInstance();
    private final String sensorId;

    public SensorReadingsResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> list() {
        return store.listReadings(sensorId);
    }

    @POST
    public Response create(SensorReading request, @Context UriInfo uriInfo) {
        SensorReading created = store.addReading(sensorId, request);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(created).build();
    }
}
