package com.gym.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class EntityManagerUtil {
    private static final String PERSISTENCE_UNIT_NAME = "GymPU";
    private static EntityManagerFactory emf;
    

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            try {
                Map<String, String> properties = new HashMap<>();
                
                properties.put("jakarta.persistence.jdbc.driver", HibernateConfig.getDriver());
                properties.put("jakarta.persistence.jdbc.url", HibernateConfig.getUrl());
                properties.put("jakarta.persistence.jdbc.user", HibernateConfig.getUsername());
                properties.put("jakarta.persistence.jdbc.password", HibernateConfig.getPassword());
                
                properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                properties.put("hibernate.show_sql", "true");
                properties.put("hibernate.format_sql", "true");
                properties.put("hibernate.hbm2ddl.auto", "update");
                
                properties.put("hibernate.c3p0.min_size", "5");
                properties.put("hibernate.c3p0.max_size", "20");
                properties.put("hibernate.c3p0.timeout", "300");
                properties.put("hibernate.c3p0.max_statements", "50");
                properties.put("hibernate.c3p0.idle_test_period", "3000");
                
                log.info("Setting up JPA with URL: {}", HibernateConfig.getUrl());
                
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
                log.info("JPA EntityManagerFactory created successfully");
            } catch (Exception e) {
                log.error("Error creating JPA EntityManagerFactory", e);
                EmailNotifier.sendDatabaseConnectionErrorNotification(
                    "Failed to create JPA EntityManagerFactory. JPA operations will not be available.", e);
                throw new RuntimeException("Error creating JPA EntityManagerFactory", e);
            }
        }
        return emf;
    }
    

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
            log.info("JPA EntityManagerFactory closed");
        }
    }
}
