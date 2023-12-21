package org.example.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.model.User;
import org.example.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Handles HTTP requests for user registration.
 * This handler processes POST requests containing user registration data.
 * First registers the user, if everything is valid,then sends a verification email.
 */
public class RegistrationHandler implements HttpHandler {

    private final UserService userService = UserService.getInstance();

    /**
     * Handles an HTTP exchange. Specifically handles POST requests for user registration.
     * Reads the request body, parses the user data from JSON, registers the user,
     * creates and sends a verification code, and sends a response back to the client.
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
            User user = gson.fromJson(json, User.class);

            // Register the user
            try {
                // Validate registration -- > if everything is valid we insert the user into the database
                userService.registerUser(user);
                // After validation we create verf. code for the user to activate his account
                String verificationCode = userService.createAndSendVerificationCode(user.getEmail());
                // Sending the verification code via email
                userService.sendVerificationEmail(user.getEmail(), verificationCode);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String response = "Registration successful";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
