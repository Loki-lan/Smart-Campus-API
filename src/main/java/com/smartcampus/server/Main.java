package com.smartcampus.server;

import com.smartcampus.api.SmartCampusApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
    // This tells Grizzly to host everything under /api/v1
    URI baseUri = URI.create("http://0.0.0.0:8080/api/v1/"); 
    
    // This loads your configuration
    SmartCampusApplication application = new SmartCampusApplication();
    
    // Start the server
    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, application);

    Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
    System.out.println("Server started at: " + baseUri.toString());
    Thread.currentThread().join();
}
}
