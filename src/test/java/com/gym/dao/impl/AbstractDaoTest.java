package com.gym.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AbstractDaoTest {

    static class TestEntity {
        private Long id;
        
        public TestEntity() {}
        
        public TestEntity(Long id) {
            this.id = id;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
    }
    
    static class TestDao extends AbstractDao<TestEntity, Long> {
        public TestDao(EntityManagerFactory emf) {
            super(TestEntity.class);
        }
        
        @Override
        protected EntityManager getEntityManager() {
            return mockEntityManager;
        }
        
        private static EntityManager mockEntityManager;
        
        public static void setMockEntityManager(EntityManager em) {
            mockEntityManager = em;
        }
    }
    
    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private CriteriaBuilder mockCriteriaBuilder;
    
    @Mock
    private CriteriaQuery<TestEntity> mockCriteriaQuery;
    
    @Mock
    private Root<TestEntity> mockRoot;
    
    @Mock
    private TypedQuery<TestEntity> mockTypedQuery;
    
    private TestDao testDao;
    private TestEntity testEntity;
    
    @BeforeEach
    void setUp() {
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(TestEntity.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(TestEntity.class)).thenReturn(mockRoot);
        when(mockCriteriaQuery.select(any())).thenReturn(mockCriteriaQuery);
        when(mockEm.createQuery(mockCriteriaQuery)).thenReturn(mockTypedQuery);
        
        TestDao.setMockEntityManager(mockEm);
        
        testDao = new TestDao(mockEmf);
        
        testEntity = new TestEntity(1L);
    }
    
    @Test
    void testSave_NewEntity() {
        TestEntity newEntity = new TestEntity(); 
        
        testDao.save(newEntity);
        
        verify(mockTransaction).begin();
        verify(mockEm).persist(newEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testSave_ExistingEntity() {
        when(mockEm.merge(testEntity)).thenReturn(testEntity);
        
        TestEntity result = testDao.save(testEntity);
        
        verify(mockTransaction).begin();
        verify(mockEm).merge(testEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
        assertEquals(testEntity, result);
    }
    
    @Test
    void testSave_WithException() {
        doThrow(new RuntimeException("Test exception")).when(mockEm).persist(any());
        when(mockTransaction.isActive()).thenReturn(true);
        
        assertThrows(RuntimeException.class, () -> testDao.save(new TestEntity()));
        
        verify(mockTransaction).begin();
        verify(mockTransaction).rollback();
        verify(mockEm).close();
    }
    
    @Test
    void testFindById_Found() {
        when(mockEm.find(TestEntity.class, 1L)).thenReturn(testEntity);
        
        Optional<TestEntity> result = testDao.findById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
        verify(mockEm).close();
    }
    
    @Test
    void testFindById_NotFound() {
        when(mockEm.find(TestEntity.class, 999L)).thenReturn(null);
        
        Optional<TestEntity> result = testDao.findById(999L);
        
        assertFalse(result.isPresent());
        verify(mockEm).close();
    }
    
    @Test
    void testFindAll() {
        List<TestEntity> expectedEntities = Arrays.asList(
            new TestEntity(1L),
            new TestEntity(2L)
        );
        when(mockTypedQuery.getResultList()).thenReturn(expectedEntities);
        
        
        List<TestEntity> result = testDao.findAll();
        
        
        assertEquals(expectedEntities, result);
        verify(mockEm).close();
    }
    
    @Test
    void testDelete() {
        
        when(mockEm.contains(testEntity)).thenReturn(true);
        
        
        testDao.delete(testEntity);
        
        
        verify(mockTransaction).begin();
        verify(mockEm).remove(testEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testDelete_EntityNotManaged() {
        
        when(mockEm.contains(testEntity)).thenReturn(false);
        when(mockEm.merge(testEntity)).thenReturn(testEntity);
        
        
        testDao.delete(testEntity);
        
        
        verify(mockTransaction).begin();
        verify(mockEm).merge(testEntity);
        verify(mockEm).remove(testEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testDelete_WithException() {
        
        when(mockEm.contains(testEntity)).thenReturn(true);
        doThrow(new RuntimeException("Test exception")).when(mockEm).remove(any());
        when(mockTransaction.isActive()).thenReturn(true);
        
        
        assertThrows(RuntimeException.class, () -> testDao.delete(testEntity));
        
        
        verify(mockTransaction).begin();
        verify(mockTransaction).rollback();
        verify(mockEm).close();
    }
    
    @Test
    void testDeleteById() {
        
        when(mockEm.find(TestEntity.class, 1L)).thenReturn(testEntity);
        when(mockEm.contains(testEntity)).thenReturn(true);
        
        
        testDao.deleteById(1L);
        
        
        verify(mockTransaction).begin();
        verify(mockEm).remove(testEntity);
        verify(mockTransaction).commit();
        
        verify(mockEm, times(2)).close();
    }
    
    @Test
    void testUpdate() {
        
        when(mockEm.merge(testEntity)).thenReturn(testEntity);
        
        
        TestEntity result = testDao.update(testEntity);
        
        
        verify(mockTransaction).begin();
        verify(mockEm).merge(testEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
        assertEquals(testEntity, result);
    }
    
    @Test
    void testUpdate_WithException() {
        
        doThrow(new RuntimeException("Test exception")).when(mockEm).merge(any());
        when(mockTransaction.isActive()).thenReturn(true);
        
        
        assertThrows(RuntimeException.class, () -> testDao.update(testEntity));
        
        
        verify(mockTransaction).begin();
        verify(mockTransaction).rollback();
        verify(mockEm).close();
    }
    
    @Test
    void testExists_True() {
        when(mockEm.find(TestEntity.class, 1L)).thenReturn(testEntity);
        
        boolean result = testDao.exists(1L);
        
        assertTrue(result);
        verify(mockEm).close();
    }
    
    @Test
    void testExists_False() {
        when(mockEm.find(TestEntity.class, 999L)).thenReturn(null);
        
        boolean result = testDao.exists(999L);
        
        assertFalse(result);
        verify(mockEm).close();
    }
    
    @Test
    void testSave_TransactionAlreadyActive() {
        TestEntity newEntity = new TestEntity(); 
        when(mockTransaction.isActive()).thenReturn(true, false); 
        
        testDao.save(newEntity);
        
        verify(mockEm).persist(newEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testIsNew_ExceptionCase() {

        TestEntity mockEntity = mock(TestEntity.class);
        when(mockEntity.getId()).thenThrow(new RuntimeException("Test exception"));
        
        TestEntity result = testDao.save(mockEntity);
        
        verify(mockTransaction).begin();
        verify(mockEm).persist(mockEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testDelete_TransactionAlreadyActive() {
        
        when(mockEm.contains(testEntity)).thenReturn(true);
        when(mockTransaction.isActive()).thenReturn(true, false); 
        
        
        testDao.delete(testEntity);
        
        
        verify(mockEm).remove(testEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testUpdate_TransactionAlreadyActive() {
        
        when(mockEm.merge(testEntity)).thenReturn(testEntity);
        when(mockTransaction.isActive()).thenReturn(true, false); 
        
        
        TestEntity updatedEntity = testDao.update(testEntity);
        
        
        verify(mockEm).merge(testEntity);
        verify(mockTransaction).commit();
        verify(mockEm).close();
        assertEquals(testEntity, updatedEntity);
    }
    
    @Test
    void testGetSession() {
        
        Session mockSession = mock(Session.class);
        when(mockEm.unwrap(Session.class)).thenReturn(mockSession);
        
        
        Session session = testDao.getSession();
        
        
        assertNotNull(session);
        assertEquals(mockSession, session);
        verify(mockEm).unwrap(Session.class);
    }
}
