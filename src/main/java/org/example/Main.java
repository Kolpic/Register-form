package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.handler.LoginHandler;
import org.example.handler.RegistrationHandler;
import org.example.handler.StaticFileHandler;
import org.example.handler.VerificationHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

/**
 * This class sets up and starts an HTTP server listening on a specified port
 * and configures endpoints for user registration, login, and verification functionalities.
 * Static resources are served from a designated directory.
 *
 * End-points:
 * For registration: http://localhost:8080/register.html
 * For login: http://localhost:8080/log.html
 */

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        // Create an HTTP server listening on localhost at port 8080.
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

        // Set up context paths and their corresponding handlers.
        server.createContext("/", new StaticFileHandler("src/main/resources/static"));
        server.createContext("/registration", new RegistrationHandler());
        server.createContext("/login-endpoint", new LoginHandler());
        server.createContext("/verify", new VerificationHandler());

        // Start the server.
        server.start();
        System.out.println("Server started!");
    }
}