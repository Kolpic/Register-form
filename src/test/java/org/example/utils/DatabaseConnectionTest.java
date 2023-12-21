package org.example.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is not exactly a unit test, more like integration test, but
 * I want to test the connection with the database
 */
class DatabaseConnectionTest {

    @Test
    void testGetConnection() throws SQLException {
        // WHEN
        // Attempt to get a connection
        Connection connection = DatabaseConnection.getConnection();

        // THEN
        // Check if the connection is not null
        assertNotNull(connection, "Database connection should not be null");

        // Close the connection
        if (connection != null) {
            connection.close();
        }
    }
}