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

/**
 * Handles HTTP requests for user verification.
 * This handler processes POST requests containing verification data (email and verification code),
 * verifies the user, and responds with the verification status.
 */
public class VerificationHandler implements HttpHandler {

    private final UserService userService = UserService.getInstance();

    /**
     * Handles an HTTP exchange for a user verification request. Specifically handles POST requests
     * by reading the request body, parsing the verification data from JSON, authenticating the user's verification code
     * using UserService, and sending a response back to the client.
     *
     * @param exchange The HTTP exchange containing the request from the client and used to send the response.
     * @throws IOException if there is an issue reading the request or sending the response.
     */
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
