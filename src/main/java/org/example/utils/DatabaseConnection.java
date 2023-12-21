package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides database connection functionality.
 * This utility class manages connections to the database and allows for the use of a custom connection,
 * which can be particularly useful in testing scenarios.
 */
public class DatabaseConnection {

    private static Connection connection = null;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/user_registration";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "1234";

    /**
     * Returns a database connection.
     * If a custom connection has been set using {@link #setConnection(Connection)}, it returns the custom connection.
     * Otherwise, it creates and returns a new MySQL connection.
     *
     * @return A {@link Connection} object representing the database connection.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection; // Use the custom connection if set
        } else {
            // Create a new MySQL connection only if the custom connection hasn't been set
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        }
    }
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
//    }

    /**
     * Sets a custom database connection.
     * This method is primarily used for setting a mock or in-memory database connection for testing purposes.
     *
     * @param customConnection A {@link Connection} object to be used as the database connection.
     */
    public static void setConnection(Connection customConnection) {
        connection = customConnection;
    }
}
