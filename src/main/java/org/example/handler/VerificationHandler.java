package org.example.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.model.User;
import org.example.model.dto.UserForVerificationDTO;
import org.example.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

public class VerificationHandler implements HttpHandler {

    private final UserService userService = UserService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            // Process POST request
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            // Parse JSON to User
            Gson gson = new Gson();
            UserForVerificationDTO user = gson.fromJson(json, UserForVerificationDTO.class);

            // Verify the user
            String response;
            int responseCode;
            try {
                boolean isVerified = userService.verifyUserEmail(user.getEmail(), user.getVerificationCode());
                if (isVerified) {
                    response = "Verified successful";
                    responseCode = 200;
                } else {
                    response = "Not verified";
                    responseCode = 401; // Unauthorized
                }
            } catch (SQLException e) {
                response = "Server error";
                responseCode = 500; // Internal Server Error
                e.printStackTrace(); // Log the exception
            }
            exchange.sendResponseHeaders(responseCode, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
