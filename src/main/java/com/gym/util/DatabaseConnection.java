package com.gym.util;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
public class DatabaseConnection {
    private static Connection singletonConnection;
    
    static {
        try {
            Class.forName(HibernateConfig.getDriver());
            log.info("PostgreSQL JDBC Driver successfully registered");
        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL JDBC Driver not found", e);
            com.gym.util.EmailNotifier.sendDatabaseConnectionErrorNotification(
                    "PostgreSQL JDBC Driver not found. Database operations will not be available.", e);
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }
    

    public static Connection getSingletonConnection() throws SQLException {
        if (singletonConnection == null || singletonConnection.isClosed()) {
            singletonConnection = createNewConnection();
            log.info("Singleton database connection established");
        }
        return singletonConnection;
    }
    

    public static Connection createNewConnection() throws SQLException {
        String url = HibernateConfig.getUrl();
        String username = HibernateConfig.getUsername();
        String password = HibernateConfig.getPassword();
        
        log.info("Attempting to connect to database with URL: {}, username: {}", url, username);
        
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            log.info("Successfully established database connection to {}", url);
            return connection;
        } catch (SQLException e) {
            log.error("Failed to establish database connection to {}", url, e);
            com.gym.util.EmailNotifier.sendDatabaseConnectionErrorNotification(
                    "Failed to establish database connection. Possible causes: incorrect credentials, database server down, incorrect port, or network issues. Current URL: " + url, e);
            throw e;
        }
    }
    

    public static Connection getConnection() throws SQLException {
        return createNewConnection();
    }

    public static void closeSingletonConnection() {
        if (singletonConnection != null) {
            try {
                singletonConnection.close();
                singletonConnection = null;
                log.info("Singleton database connection closed");
            } catch (SQLException e) {
                log.error("Error closing singleton database connection", e);
                com.gym.util.EmailNotifier.sendDatabaseConnectionErrorNotification(
                        "Error closing singleton database connection. Potential resource leak.", e);
            }
        }
    }
    

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                log.debug("Database connection closed");
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }
}