package org.example.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.model.User;
import org.example.model.dto.LoginResponseDTO;
import org.example.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * Handles HTTP requests for user login.
 * This handler processes POST requests containing login data (email and password),
 * authenticates the user, and responds with the login status.
 */
public class LoginHandler implements HttpHandler {

    private final UserService userService = UserService.getInstance();

    /**
     * Handles an HTTP exchange for a login request. Specifically handles POST requests
     * for user login by reading the request body, parsing the user data from JSON,
     * authenticating the user using UserService, and sending a response back to the client.
     *
     * @param exchange The HTTP exchange containing the request from the client and used to send the response.
     * @throws IOException if there is an issue reading the request or sending the response.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            // Parse JSON to User object
            Gson gson = new Gson();
            User user = gson.fromJson(json, User.class);

            // Use UserService to log in the user
            String response;
            int responseCode;
            try {
                String tokenForSession = userService.loginUser(user.getEmail(), user.getPassword());
                boolean sessionValid = userService.isSessionValid(user.getEmail(), tokenForSession);
                if (tokenForSession != null && sessionValid) {
                    response = "Login successful";
                    responseCode = 200;
                } else {
                    response = "Invalid credentials";
                    responseCode = 401; // Unauthorized
                }
            } catch (SQLException e) {
                response = "Server error";
                responseCode = 500; // Internal Server Error
                e.printStackTrace(); // Log the exception
            }
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            String jsonResponse = new Gson().toJson(new LoginResponseDTO(response, user.getEmail()));

            exchange.sendResponseHeaders(responseCode, jsonResponse.length());
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        }
    }
}
