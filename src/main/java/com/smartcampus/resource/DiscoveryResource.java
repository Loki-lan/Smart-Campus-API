package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    @GET
    public Map<String, Object> discover(@Context UriInfo uriInfo) {
        URI base = uriInfo.getBaseUri();

        Map<String, Object> links = new LinkedHashMap<>();
        links.put("self", Map.of("href", base.toString()));
        links.put("rooms", Map.of("href", base.resolve("rooms").toString()));
        links.put("sensors", Map.of("href", base.resolve("sensors").toString()));
        links.put("sensorReadings", Map.of("href", base.toString() + "sensors/{sensorId}/readings", "templated", true));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "Smart Campus API");
        response.put("version", "v1");
        response.put("_links", links);
        return response;
    }
}
