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

/**
 * Repository class for managing user-related data persistence.
 * Provides methods for saving users to the database, storing verification codes,
 * verifying users, logging in users, and retrieving hashed passwords.
 */
public class UserRepository {

    private static UserRepository userRepository;

    /**
     * Singleton pattern to ensure only one instance of UserRepository exists.
     *
     * @return The single instance of UserRepository.
     */
    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    /**
     * Saves a user's data to the database.
     *
     * @param username The user's name.
     * @param email The user's email.
     * @param password The user's hashed password.
     * @throws SQLException If a database access error occurs.
     */
    public void saveUserToDatabase(String username, String email, String password) throws SQLException {
        // Store user in the database
        try (Connection connection = DatabaseConnection.getConnection()){
            String sqlStatement = "INSERT INTO users (name, email, password, verification_status) " +
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

    /**
     * Stores a verification code for a user in the database.
     *
     * @param verificationCode The verification code to store.
     * @param email The email of the user to whom the code belongs.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Verifies a user's email using the provided verification code.
     *
     * @param email The user's email to verify.
     * @param verificationCode The verification code for validation.
     * @return true if the verification is successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Authenticates a user's login based on email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return A session token if login is successful, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Retrieves the hashed password of a user based on their email.
     *
     * @param email The email of the user whose password is to be retrieved.
     * @return The hashed password of the user.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Updates the verification status of a user to true.
     *
     * @param email The email of the user whose status is to be updated.
     * @throws SQLException If a database access error occurs.
     */
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
