package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Handles HTTP requests for static files.
 * This handler serves static files (like HTML, CSS, JS) from a specified root directory.
 */
public class StaticFileHandler implements HttpHandler {

    private final String rootPath;

    /**
     * Constructs a new StaticFileHandler with the specified root directory path.
     *
     * @param rootPath The root directory path where static files are located.
     */
    public StaticFileHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Handles an HTTP exchange by serving a static file.
     * The file to serve is determined by appending the request URI to the root path.
     * If the file does not exist, a 404 (Not Found) response is sent.
     * Otherwise, the file is sent with a 200 (OK) response.
     *
     * @param exchange The HTTP exchange containing the request from the client and used to send the response.
     * @throws IOException if there is an issue reading the file or sending the response.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String filePath = rootPath + exchange.getRequestURI().getPath();
        File file = new File(filePath);
        if (!file.exists()) {
            exchange.sendResponseHeaders(404,0);
            exchange.close();
        }

        exchange.sendResponseHeaders(200, file.length());
        try (OutputStream outputStream = exchange.getResponseBody()) {
            Files.copy(file.toPath(), outputStream);
        }
    }
}
