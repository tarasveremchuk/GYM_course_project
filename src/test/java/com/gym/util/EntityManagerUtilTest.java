package com.gym.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEFAULTS;

@ExtendWith(MockitoExtension.class)
public class EntityManagerUtilTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEntityManager;
    
    private MockedStatic<Persistence> mockedPersistence;
    private MockedStatic<HibernateConfig> mockedHibernateConfig;
    private MockedStatic<EmailNotifier> mockedEmailNotifier;
    
    @BeforeEach
    void setUp() throws Exception {
        
        resetStaticFields();
        
        
        mockedPersistence = mockStatic(Persistence.class, RETURNS_DEFAULTS);
        mockedHibernateConfig = mockStatic(HibernateConfig.class, RETURNS_DEFAULTS);
        mockedEmailNotifier = mockStatic(EmailNotifier.class, RETURNS_DEFAULTS);
        
        
        mockedHibernateConfig.when(HibernateConfig::getUrl).thenReturn("jdbc:postgresql://localhost:5433/gym");
        mockedHibernateConfig.when(HibernateConfig::getUsername).thenReturn("postgres");
        mockedHibernateConfig.when(HibernateConfig::getPassword).thenReturn("taras123");
        mockedHibernateConfig.when(HibernateConfig::getDriver).thenReturn("org.postgresql.Driver");
        
        
        mockedPersistence.when(() -> Persistence.createEntityManagerFactory(anyString(), any(Map.class)))
            .thenReturn(mockEmf);
        
        
        lenient().when(mockEmf.createEntityManager()).thenReturn(mockEntityManager);
        lenient().when(mockEmf.isOpen()).thenReturn(true);
    }
    
    @AfterEach
    void tearDown() {
        
        if (mockedPersistence != null) {
            mockedPersistence.close();
        }
        if (mockedHibernateConfig != null) {
            mockedHibernateConfig.close();
        }
        if (mockedEmailNotifier != null) {
            mockedEmailNotifier.close();
        }
    }
    
    
    private void resetStaticFields() throws Exception {
        Field emfField = EntityManagerUtil.class.getDeclaredField("emf");
        emfField.setAccessible(true);
        emfField.set(null, null);
    }
    
    @Test
    void testGetEntityManagerFactory() {
        
        try {
            Field field = EntityManagerUtil.class.getDeclaredField("emf");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset static field: " + e.getMessage());
        }
        
        
        EntityManagerFactory result = EntityManagerUtil.getEntityManagerFactory();
        
        
        assertSame(mockEmf, result);
        
        
        mockedHibernateConfig.verify(() -> HibernateConfig.getUrl(), atLeastOnce());
        
        
        mockedPersistence.verify(() -> 
            Persistence.createEntityManagerFactory(anyString(), any(Map.class)), atLeastOnce());
    }
    
    @Test
    void testGetEntityManagerFactoryWhenAlreadyExists() throws Exception {
        
        Field emfField = EntityManagerUtil.class.getDeclaredField("emf");
        emfField.setAccessible(true);
        emfField.set(null, mockEmf);
        
        
        EntityManagerFactory result = EntityManagerUtil.getEntityManagerFactory();
        
        
        assertSame(mockEmf, result);
        
        
        mockedPersistence.verify(() -> 
            Persistence.createEntityManagerFactory(anyString(), any(Map.class)), 
            never());
    }
    
    @Test
    void testGetEntityManagerFactoryWithException() {
        
        mockedPersistence.when(() -> Persistence.createEntityManagerFactory(anyString(), any(Map.class)))
            .thenThrow(new RuntimeException("Test exception"));
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            EntityManagerUtil.getEntityManagerFactory();
        });
        
        
        assertEquals("Error creating JPA EntityManagerFactory", exception.getMessage());
        
        
        mockedEmailNotifier.verify(() -> 
            EmailNotifier.sendDatabaseConnectionErrorNotification(
                contains("Failed to create JPA EntityManagerFactory"), 
                any(RuntimeException.class)), 
            times(1));
    }
    
    @Test
    void testCreateEntityManager() {
        
        EntityManager result = EntityManagerUtil.createEntityManager();
        
        
        assertSame(mockEntityManager, result);
        
        
        mockedPersistence.verify(() -> 
            Persistence.createEntityManagerFactory(anyString(), any(Map.class)), 
            times(1));
        
        
        verify(mockEmf, times(1)).createEntityManager();
    }
    
    @Test
    void testCloseEntityManagerFactory() throws Exception {
        
        Field emfField = EntityManagerUtil.class.getDeclaredField("emf");
        emfField.setAccessible(true);
        emfField.set(null, mockEmf);
        
        
        EntityManagerUtil.closeEntityManagerFactory();
        
        
        verify(mockEmf, times(1)).isOpen();
        verify(mockEmf, times(1)).close();
        
        
        assertNull(emfField.get(null));
    }
    
    @Test
    void testCloseEntityManagerFactoryWhenAlreadyClosed() throws Exception {
        
        Field emfField = EntityManagerUtil.class.getDeclaredField("emf");
        emfField.setAccessible(true);
        emfField.set(null, mockEmf);
        
        
        when(mockEmf.isOpen()).thenReturn(false);
        
        
        EntityManagerUtil.closeEntityManagerFactory();
        
        
        verify(mockEmf, times(1)).isOpen();
        verify(mockEmf, never()).close();
        
        
        assertSame(mockEmf, emfField.get(null));
    }
    
    @Test
    void testCloseEntityManagerFactoryWhenNull() {
        
        EntityManagerUtil.closeEntityManagerFactory();
        
        
    }
}
