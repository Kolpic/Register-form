package org.example.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {

    private final String rootPath;

    public StaticFileHandler(String rootPath) {
        this.rootPath = rootPath;
    }

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
