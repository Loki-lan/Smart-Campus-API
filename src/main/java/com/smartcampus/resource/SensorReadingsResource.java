package com.smartcampus.resource;

import com.smartcampus.model.SensorReading;
import com.smartcampus.store.SmartCampusStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
