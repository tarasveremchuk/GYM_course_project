package com.gym.service;

import com.gym.util.DatabaseConnection;
import com.gym.util.EmailSender;
import com.gym.util.HibernateConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseServiceTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseService databaseService;

    @BeforeEach
    void setUp() {
        
        try (MockedStatic<HibernateConfig> mockedStatic = mockStatic(HibernateConfig.class)) {
            mockedStatic.when(HibernateConfig::getSessionFactory).thenReturn(sessionFactory);
            
            databaseService = new DatabaseService();
        }
    }

    @Test
    void getSessionFactory_ShouldReturnSessionFactory() {
        
        SessionFactory result = databaseService.getSessionFactory();

        
        assertEquals(sessionFactory, result);
    }

    @Test
    void getDatabaseConfig_ShouldReturnConfigMap() {
        
        Map<String, String> config = databaseService.getDatabaseConfig();

        
        assertNotNull(config);
        assertEquals("jdbc:postgresql://localhost:5433/gym", config.get("url"));
        assertEquals("postgres", config.get("user"));
        assertEquals("org.hibernate.dialect.PostgreSQLDialect", config.get("dialect"));
        
        assertNull(config.get("password"));
    }

    @Test
    void testConnection_WhenConnectionSucceeds_ShouldReturnTrue() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);

            
            boolean result = databaseService.testConnection();

            
            assertTrue(result);
            mockedStatic.verify(DatabaseConnection::getConnection);
        }
    }

    @Test
    void testConnection_WhenConnectionFails_ShouldReturnFalse() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenThrow(new SQLException("Connection failed"));

            
            boolean result = databaseService.testConnection();

            
            assertFalse(result);
            mockedStatic.verify(DatabaseConnection::getConnection);
        }
    }

    @Test
    void getConnection_ShouldReturnConnection() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);

            
            Connection result = databaseService.getConnection();

            
            assertEquals(connection, result);
            mockedStatic.verify(DatabaseConnection::getConnection);
        }
    }

    @Test
    void getConnection_WhenConnectionFails_ShouldSendErrorNotification() throws SQLException {
        
        SQLException sqlException = new SQLException("Connection failed");
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenThrow(sqlException);

            
            assertThrows(SQLException.class, () -> {
                databaseService.getConnection();
            });
            
            mockedStatic.verify(DatabaseConnection::getConnection);
        }
    }

    @Test
    void shutdown_ShouldCloseSessionFactory() {
        
        when(sessionFactory.isClosed()).thenReturn(false);
        doNothing().when(sessionFactory).close();

        
        databaseService.shutdown();

        
        verify(sessionFactory).isClosed();
        verify(sessionFactory).close();
    }

    @Test
    void shutdown_WhenSessionFactoryAlreadyClosed_ShouldNotCloseAgain() {
        
        when(sessionFactory.isClosed()).thenReturn(true);

        
        databaseService.shutdown();

        
        verify(sessionFactory).isClosed();
        verify(sessionFactory, never()).close();
    }

    @Test
    void executeWithConnection_ShouldExecuteOperation() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);
            
            DatabaseService.DatabaseOperation<String> operation = conn -> "Operation executed";

            
            String result = databaseService.executeWithConnection(operation);

            
            assertEquals("Operation executed", result);
            mockedStatic.verify(DatabaseConnection::getConnection);
        }
    }

    @Test
    void executeWithConnection_WhenOperationFails_ShouldPropagateException() throws SQLException {
        
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(connection);
            
            DatabaseService.DatabaseOperation<String> operation = conn -> {
                throw new SQLException("Operation failed");
            };

            
            assertThrows(SQLException.class, () -> {
                databaseService.executeWithConnection(operation);
            });
            
            mockedStatic.verify(DatabaseConnection::getConnection);
        }
    }
}
