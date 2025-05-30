package com.gym.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import com.gym.model.User;
import com.gym.model.Client;
import com.gym.model.Staff;
import com.gym.model.Membership;
import com.gym.model.Payment;
import com.gym.model.Visit;
import com.gym.model.Training;
import com.gym.model.Booking;
import com.gym.model.ClientTrainer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class HibernateConfig {
    private static final String PROPERTIES_FILE = "database.properties";
    private static Properties dbProperties = new Properties();
    
    private static SessionFactory sessionFactory;
    
    static {
        loadProperties();
    }
    

    private static void loadProperties() {
        try (InputStream input = HibernateConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                log.error("Unable to find {} file", PROPERTIES_FILE);
                EmailNotifier.sendDatabaseConnectionErrorNotification(
                    "Unable to find database properties file. Database operations will not be available.", 
                    new RuntimeException("Properties file not found"));
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            
            dbProperties.load(input);
            log.info("Database properties loaded successfully from {}", PROPERTIES_FILE);
        } catch (IOException e) {
            log.error("Error loading database properties", e);
            EmailNotifier.sendDatabaseConnectionErrorNotification(
                "Error loading database properties. Database operations will not be available.", e);
            throw new RuntimeException("Error loading database properties", e);
        }
    }
    

    public static String getUrl() {
        String url = dbProperties.getProperty("db.url");
        log.info("Using database URL: {}", url);
        return url;
    }
    

    public static String getUsername() {
        String username = dbProperties.getProperty("db.username");
        log.info("Using database username: {}", username);
        return username;
    }
    

    public static String getPassword() {
        String password = dbProperties.getProperty("db.password");
        log.info("Using database password: {}", password.replaceAll(".", "*"));
        return password;
    }
    

    public static String getDriver() {
        String driver = dbProperties.getProperty("db.driver");
        log.info("Using database driver: {}", driver);
        return driver;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, getDriver());
                settings.put(Environment.URL, getUrl());
                settings.put(Environment.USER, getUsername());
                settings.put(Environment.PASS, getPassword());
                settings.put(Environment.DIALECT, dbProperties.getProperty("hibernate.dialect"));
                settings.put(Environment.SHOW_SQL, dbProperties.getProperty("hibernate.show_sql"));
                settings.put(Environment.FORMAT_SQL, dbProperties.getProperty("hibernate.format_sql"));
                settings.put(Environment.HBM2DDL_AUTO, dbProperties.getProperty("hibernate.hbm2ddl.auto"));
                
                settings.put(Environment.C3P0_MIN_SIZE, dbProperties.getProperty("connection.pool.min_size"));
                settings.put(Environment.C3P0_MAX_SIZE, dbProperties.getProperty("connection.pool.max_size"));
                settings.put(Environment.C3P0_TIMEOUT, dbProperties.getProperty("connection.pool.timeout"));
                settings.put(Environment.C3P0_MAX_STATEMENTS, dbProperties.getProperty("connection.pool.max_statements"));
                settings.put(Environment.C3P0_IDLE_TEST_PERIOD, dbProperties.getProperty("connection.pool.idle_test_period"));
                
                configuration.setProperties(settings);
                
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Client.class);
                configuration.addAnnotatedClass(Staff.class);
                configuration.addAnnotatedClass(Membership.class);
                configuration.addAnnotatedClass(Payment.class);
                configuration.addAnnotatedClass(Visit.class);
                configuration.addAnnotatedClass(Training.class);
                configuration.addAnnotatedClass(Booking.class);
                configuration.addAnnotatedClass(ClientTrainer.class);
                
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                log.info("Hibernate SessionFactory created successfully");
            } catch (Exception e) {
                log.error("Error initializing Hibernate SessionFactory", e);
                com.gym.util.EmailNotifier.sendHibernateErrorNotification(
                        "Failed to initialize Hibernate SessionFactory. Application database operations will not work correctly.", e);
                throw new RuntimeException("Error initializing Hibernate SessionFactory", e);
            }
        }
        return sessionFactory;
    }
    

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("Hibernate SessionFactory closed");
        }
    }
}
