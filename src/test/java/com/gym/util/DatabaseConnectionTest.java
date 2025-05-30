package com.gym.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEFAULTS;

@ExtendWith(MockitoExtension.class)
public class DatabaseConnectionTest {

    private Connection mockConnection;
    private MockedStatic<DriverManager> mockedDriverManager;
    private MockedStatic<HibernateConfig> mockedHibernateConfig;
    private MockedStatic<EmailNotifier> mockedEmailNotifier;

    @BeforeEach
    void setUp() throws Exception {
        
        mockConnection = mock(Connection.class);
        
        
        mockedDriverManager = mockStatic(DriverManager.class, RETURNS_DEFAULTS);
        mockedEmailNotifier = mockStatic(EmailNotifier.class, RETURNS_DEFAULTS);
        mockedHibernateConfig = mockStatic(HibernateConfig.class);
        
        
        mockedHibernateConfig.when(HibernateConfig::getUrl).thenReturn("jdbc:postgresql://localhost:5433/gym");
        mockedHibernateConfig.when(HibernateConfig::getUsername).thenReturn("postgres");
        mockedHibernateConfig.when(HibernateConfig::getPassword).thenReturn("taras123");
        mockedHibernateConfig.when(HibernateConfig::getDriver).thenReturn("org.postgresql.Driver");
        
        
        mockedDriverManager.when(() -> 
            DriverManager.getConnection(anyString(), anyString(), anyString()))
            .thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        
        if (mockedDriverManager != null) {
            mockedDriverManager.close();
        }
        if (mockedHibernateConfig != null) {
            mockedHibernateConfig.close();
        }
        if (mockedEmailNotifier != null) {
            mockedEmailNotifier.close();
        }
    }

    @Test
    void testGetSingletonConnection() throws SQLException, NoSuchFieldException, IllegalAccessException {
        
        Field singletonConnectionField = DatabaseConnection.class.getDeclaredField("singletonConnection");
        singletonConnectionField.setAccessible(true);
        singletonConnectionField.set(null, null);
        
        
        mockedDriverManager.when(() -> 
            DriverManager.getConnection(anyString(), anyString(), anyString())
        ).thenReturn(mockConnection);
        
        
        Connection connection1 = DatabaseConnection.getSingletonConnection();
        assertNotNull(connection1);
        
        
        when(mockConnection.isClosed()).thenReturn(false);
        
        
        Connection connection2 = DatabaseConnection.getSingletonConnection();
        assertNotNull(connection2);
        
        
        
        mockedDriverManager.verify(() -> 
            DriverManager.getConnection(anyString(), anyString(), anyString()), 
            times(1));
    }

    @Test
    void testGetSingletonConnectionWhenClosed() throws SQLException {
        
        try {
            Field singletonConnectionField = DatabaseConnection.class.getDeclaredField("singletonConnection");
            singletonConnectionField.setAccessible(true);
            singletonConnectionField.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset singletonConnection field: " + e.getMessage());
        }
        
        
        mockedDriverManager.when(() -> 
            DriverManager.getConnection(anyString(), anyString(), anyString())
        ).thenReturn(mockConnection);
        
        
        Connection connection1 = DatabaseConnection.getSingletonConnection();
        assertNotNull(connection1);
        
        
        when(mockConnection.isClosed()).thenReturn(true);
        
        
        Connection connection2 = DatabaseConnection.getSingletonConnection();
        assertNotNull(connection2);
        
        
        
        mockedDriverManager.verify(() -> 
            DriverManager.getConnection(anyString(), anyString(), anyString()), 
            atLeast(1));
    }

    @Test
    void testCreateNewConnection() throws SQLException {
        
        Connection connection = DatabaseConnection.createNewConnection();
        assertNotNull(connection);
        
        
        mockedDriverManager.verify(() -> 
            DriverManager.getConnection(
                eq("jdbc:postgresql://localhost:5433/gym"), 
                eq("postgres"), 
                eq("taras123")), 
            times(1));
    }

    @Test
    void testCreateNewConnectionWithException() throws Exception {
        
        if (mockedDriverManager != null) {
            mockedDriverManager.close();
        }
        
        
        SQLException sqlException = new SQLException("Test connection error");
        mockedDriverManager = mockStatic(DriverManager.class);
        mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
            .thenThrow(sqlException);
        
        
        assertThrows(SQLException.class, () -> {
            DatabaseConnection.createNewConnection();
        });
        
        
        mockedEmailNotifier.verify(() -> 
            EmailNotifier.sendDatabaseConnectionErrorNotification(
                anyString(), any(SQLException.class)));
    }

    @Test
    void testGetConnection() throws SQLException {
        
        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection);
        
        
        mockedDriverManager.verify(() -> 
            DriverManager.getConnection(anyString(), anyString(), anyString()), 
            times(1));
    }

    @Test
    void testCloseSingletonConnection() throws Exception {
        
        Connection connection = DatabaseConnection.getSingletonConnection();
        assertNotNull(connection);
        
        
        DatabaseConnection.closeSingletonConnection();
        
        
        verify(mockConnection).close();
        
        
        reset(mockConnection);
        
        
        Connection newConnection = DatabaseConnection.getSingletonConnection();
        assertNotNull(newConnection);
        
        
    }

    @Test
    void testCloseSingletonConnectionWithException() throws SQLException, NoSuchFieldException, IllegalAccessException {
        
        Field singletonConnectionField = DatabaseConnection.class.getDeclaredField("singletonConnection");
        singletonConnectionField.setAccessible(true);
        
        
        singletonConnectionField.set(null, mockConnection);
        
        
        doThrow(new SQLException("Close error")).when(mockConnection).close();
        
        
        DatabaseConnection.closeSingletonConnection();
        
        
        verify(mockConnection).close();
        
        
        mockedEmailNotifier.verify(() -> 
            EmailNotifier.sendDatabaseConnectionErrorNotification(
                contains("Error closing singleton database connection"), 
                any(SQLException.class)));
    }

    @Test
    void testCloseConnection() throws SQLException {
        
        DatabaseConnection.closeConnection(mockConnection);
        
        
        verify(mockConnection, times(1)).close();
    }

    @Test
    void testCloseConnectionWithException() throws SQLException {
        
        doThrow(new SQLException("Close error")).when(mockConnection).close();
        
        
        DatabaseConnection.closeConnection(mockConnection);
        
        
        verify(mockConnection, times(1)).close();
    }

    @Test
    void testCloseConnectionWithNull() {
        
        DatabaseConnection.closeConnection(null);
        
        
    }
    
    
    private static <T> T eq(T value) {
        return Mockito.eq(value);
    }
}
