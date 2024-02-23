package org.example.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.exception.InvalidUserInputData;
import org.example.model.User;
import org.example.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

public class RegistrationHandler implements HttpHandler {

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
