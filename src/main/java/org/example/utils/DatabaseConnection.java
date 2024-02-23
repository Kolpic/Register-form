package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection = null;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/user_registration";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "1234";

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

    public static void setConnection(Connection customConnection) {
        connection = customConnection;
    }
}
