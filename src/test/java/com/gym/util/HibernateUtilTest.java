package com.gym.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HibernateUtilTest {

    @Mock
    private SessionFactory mockSessionFactory;
    
    @Mock
    private Session mockSession;
    
    @Mock
    private Transaction mockTransaction;
    
    private MockedStatic<HibernateConfig> mockedHibernateConfig;
    
    @BeforeEach
    void setUp() {
        
        mockedHibernateConfig = mockStatic(HibernateConfig.class);
        
        
        mockedHibernateConfig.when(HibernateConfig::getSessionFactory).thenReturn(mockSessionFactory);
        
        
        lenient().when(mockSessionFactory.openSession()).thenReturn(mockSession);
        
        
        lenient().when(mockSession.beginTransaction()).thenReturn(mockTransaction);
    }
    
    @AfterEach
    void tearDown() {
        
        if (mockedHibernateConfig != null) {
            mockedHibernateConfig.close();
        }
    }
    
    @Test
    void testDoInTransaction() {
        
        @SuppressWarnings("unchecked")
        Consumer<Session> mockOperation = mock(Consumer.class);
        
        
        HibernateUtil.doInTransaction(mockOperation);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockOperation).accept(mockSession);
        
        
        verify(mockTransaction).commit();
    }
    
    @Test
    void testDoInTransactionWithException() {
        
        Consumer<Session> mockOperation = session -> {
            throw new RuntimeException("Test exception");
        };
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            HibernateUtil.doInTransaction(mockOperation);
        });
        
        
        assertEquals("Transaction failed", exception.getMessage());
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockTransaction).rollback();
    }
    
    @Test
    void testDoInTransactionWithResult() {
        
        Function<Session, String> mockOperation = session -> "Test result";
        
        
        String result = HibernateUtil.doInTransactionWithResult(mockOperation);
        
        
        assertEquals("Test result", result);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockTransaction).commit();
    }
    
    @Test
    void testDoInTransactionWithResultException() {
        
        Function<Session, String> mockOperation = session -> {
            throw new RuntimeException("Test exception");
        };
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            HibernateUtil.doInTransactionWithResult(mockOperation);
        });
        
        
        assertEquals("Transaction failed", exception.getMessage());
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockTransaction).rollback();
    }
    
    @Test
    void testDoInSession() {
        
        Function<Session, String> mockOperation = session -> "Test result";
        
        
        String result = HibernateUtil.doInSession(mockOperation);
        
        
        assertEquals("Test result", result);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession, never()).beginTransaction();
    }
    
    @Test
    void testDoInSessionWithException() {
        
        Function<Session, String> mockOperation = session -> {
            throw new RuntimeException("Test exception");
        };
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            HibernateUtil.doInSession(mockOperation);
        });
        
        
        assertEquals("Session operation failed", exception.getMessage());
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession, never()).beginTransaction();
    }
    
    @Test
    void testSave() {
        
        TestEntity entity = new TestEntity("Test entity");
        
        
        TestEntity result = HibernateUtil.save(entity);
        
        
        assertSame(entity, result);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockSession).persist(entity);
        
        
        verify(mockTransaction).commit();
    }
    
    @Test
    void testUpdate() {
        
        TestEntity entity = new TestEntity("Test entity");
        
        
        TestEntity result = HibernateUtil.update(entity);
        
        
        assertSame(entity, result);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockSession).merge(entity);
        
        
        verify(mockTransaction).commit();
    }
    
    @Test
    void testDelete() {
        
        TestEntity entity = new TestEntity("Test entity");
        
        
        HibernateUtil.delete(entity);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).beginTransaction();
        
        
        verify(mockSession).remove(entity);
        
        
        verify(mockTransaction).commit();
    }
    
    @Test
    void testFindById() {
        
        TestEntity entity = new TestEntity("Test entity");
        
        
        when(mockSession.get(TestEntity.class, 1L)).thenReturn(entity);
        
        
        TestEntity result = HibernateUtil.findById(TestEntity.class, 1L);
        
        
        assertSame(entity, result);
        
        
        verify(mockSessionFactory).openSession();
        
        
        verify(mockSession).get(TestEntity.class, 1L);
    }
    
    
    @SuppressWarnings("unused")
    private static class TestEntity {
        private String name;
        
        public TestEntity(String name) {
            this.name = name;
        }
        
        
        
    }
}
