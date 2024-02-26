package org.example.repository;

import org.example.service.UserService;
import org.example.utils.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.example.utils.SessionManager.storeSessionToken;

public class UserRepository {

    private static UserRepository userRepository;

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    public void saveUserToDatabase(String username, String email, String password) throws SQLException {
        // Store user in the database
        try (Connection connection = DatabaseConnection.getConnection()){
            String sqlStatement = "INSERT INTO users (full_name, email, password, verification_status) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
                statement.setString(1, username);
                statement.setString(2, email);
                statement.setString(3, password);
                statement.setBoolean(4, false);
                statement.execute();
            }
        }
    }

    public void storeVerificationCodeInDatabase(String verificationCode, String email) throws SQLException {
        // Store the verification code in the database
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sqlStatement = "UPDATE users SET verification_code = ? WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
                statement.setString(1, verificationCode);
                statement.setString(2, email);
                statement.execute();
            }
        }
    }

    public boolean verify(String email, String verificationCode) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sqlStatement = "SELECT verification_code FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String storedCode = resultSet.getString("verification_code");
                    if (storedCode != null && storedCode.equals(verificationCode)) {
                        updateVerificationStatus(email);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String login(String email, String password) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sqlStatement = "SELECT password, verification_status FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    boolean isVerifiedAccount = resultSet.getBoolean("verification_status");
                    // Checking if the password is correct and the account is verified
                    if (BCrypt.checkpw(password, storedPassword) && isVerifiedAccount) {
                        // Create a session token
                        String sessionToken = UUID.randomUUID().toString();

                        // Store session token
                        storeSessionToken(email, sessionToken);
                        return sessionToken; // Login successful return the token
                    }
                }
            }
        }
        return null;
    }

    public String getHashedPasswordForUser(String email) throws SQLException {
        String hashedPassword = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sqlStatement = "SELECT password FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        hashedPassword = resultSet.getString("password");
                    }
                }
            }
        }
        return hashedPassword;
    }

    private void updateVerificationStatus(String email) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sqlStatement = "UPDATE users SET verification_status = TRUE, " +
                    "verification_code = NULL WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
                statement.setString(1, email);
                statement.executeUpdate();
            }
        }
    }
}
