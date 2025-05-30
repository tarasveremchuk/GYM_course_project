package com.gym.service;

import com.gym.util.DatabaseConnection;
import com.gym.util.HibernateConfig;
import com.gym.util.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class DatabaseService {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/gym";
    private static final String DB_USER = "postgres";
    
    private final EmailSender emailSender;
    private SessionFactory sessionFactory;
    
    public DatabaseService() {
        this.emailSender = new EmailSender();
        initializeHibernate();
    }
    
  
    private void initializeHibernate() {
        try {
            log.info("Initializing Hibernate...");
            
            sessionFactory = HibernateConfig.getSessionFactory();
            log.info("Hibernate initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Hibernate: {}", e.getMessage());
            sendErrorNotification("Hibernate Initialization Failure", 
                    "Failed to initialize Hibernate: " + e.getMessage());
        }
    }
    

    public Connection getConnection() throws SQLException {
        try {
            log.debug("Getting database connection...");
            return DatabaseConnection.getConnection();
        } catch (SQLException e) {
            log.error("Failed to get database connection: {}", e.getMessage());
            sendErrorNotification("Database Connection Failure", 
                    "Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
    

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            log.info("Database connection test successful");
            return true;
        } catch (SQLException e) {
            log.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }
    

    public Map<String, String> getDatabaseConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("url", DB_URL);
        config.put("user", DB_USER);
        
        config.put("dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return config;
    }
    

    private Properties loadEmailConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                log.error("Unable to find email.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException e) {
            log.error("Failed to load email configuration: {}", e.getMessage());
        }
        return properties;
    }
    

    private void sendErrorNotification(String subject, String message) {
        try {
            Properties emailConfig = loadEmailConfig();
            String recipient = emailConfig.getProperty("mail.recipient.email");
            if (recipient != null && !recipient.isEmpty()) {
                
                String prefix = emailConfig.getProperty("mail.notification.subject.prefix", "");
                String fullSubject = prefix + " " + subject;
                
                emailSender.sendEmail(recipient, fullSubject, message);
                log.info("Error notification email sent to {}", recipient);
            } else {
                log.warn("No recipient email configured for error notifications");
            }
        } catch (Exception e) {
            log.error("Failed to send error notification email: {}", e.getMessage());
        }
    }

    public void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            log.info("Closing Hibernate session factory...");
            sessionFactory.close();
            log.info("Hibernate session factory closed");
        }
    }
    

    public <T> T executeWithConnection(DatabaseOperation<T> operation) throws SQLException {
        try (Connection conn = getConnection()) {
            return operation.execute(conn);
        } catch (SQLException e) {
            log.error("Database operation failed: {}", e.getMessage());
            throw e;
        }
    }
    

    @FunctionalInterface
    public interface DatabaseOperation<T> {
        T execute(Connection connection) throws SQLException;
    }
}
