package org.example.repository;

import org.h2.tools.RunScript;
import org.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        userRepository = UserRepository.getInstance();

        // Setup H2 database connection
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        DatabaseConnection.setConnection(connection);

        // Set up the database schema for H2
        String createTableSql =
                "CREATE TABLE IF NOT EXISTS users (" +
                        "name VARCHAR(255), " +
                        "email VARCHAR(255), " +
                        "password VARCHAR(255), " +
                        "verification_status BOOLEAN, " +
                        "verification_code VARCHAR(255)" +
                        ");";
        RunScript.execute(connection, new StringReader(createTableSql));
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Close the database connection after each test
        connection = DatabaseConnection.getConnection();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        // Reset the connection
        DatabaseConnection.setConnection(null);
    }

    @Test
    public void testSaveUserToDatabaseShouldSaveUserSuccessful() throws Exception {
        // Arrange
        String name = "Mima";
        String email = "mima@example.com";
        String password = "hashedPassword123";

        // Act
        userRepository.saveUserToDatabase(name, email, password);

        // Assert
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next(), "User should be found in the database");
            assertEquals(name, resultSet.getString("name"), "Name should match");
            assertEquals(email, resultSet.getString("email"), "Email should match");
            assertEquals(password, resultSet.getString("password"), "Password should match");
            assertFalse(resultSet.getBoolean("verification_status"), "Verification status should be false");
        }
    }
}