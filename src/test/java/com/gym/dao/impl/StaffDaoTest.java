package com.gym.dao.impl;

import com.gym.model.Staff;
import com.gym.model.StaffRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StaffDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<Staff> mockTypedQuery;
    
    @Spy
    @InjectMocks
    private StaffDao staffDao;
    
    private Staff testTrainer;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testTrainer = new Staff();
        testTrainer.setId(1L);
        testTrainer.setFullName("John Smith");
        testTrainer.setRole(StaffRole.TRAINER);
        testTrainer.setPhone("+380501234567");
        testTrainer.setEmail("john.smith@example.com");
        testTrainer.setSalary(new BigDecimal("25000.00"));
        
        
        doReturn(mockEm).when(staffDao).getEntityManager();
    }
    
    @Test
    void testFindTrainers() {
        
        List<Staff> expectedTrainers = Arrays.asList(testTrainer);
        when(mockEm.createQuery(anyString(), eq(Staff.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("role"), any(StaffRole.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainers);
        
        
        List<Staff> result = staffDao.findTrainers();
        
        
        assertEquals(expectedTrainers, result);
        verify(mockEm).createQuery(anyString(), eq(Staff.class));
        verify(mockTypedQuery).setParameter("role", StaffRole.TRAINER);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindActiveTrainers() {
        
        List<Staff> expectedTrainers = Arrays.asList(testTrainer);
        when(mockEm.createQuery(anyString(), eq(Staff.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("role"), any(StaffRole.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainers);
        
        
        List<Staff> result = staffDao.findActiveTrainers();
        
        
        assertEquals(expectedTrainers, result);
        verify(mockEm).createQuery(anyString(), eq(Staff.class));
        verify(mockTypedQuery).setParameter("role", StaffRole.TRAINER);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindTrainerByClientId_Found() {
        
        List<Staff> expectedTrainers = Arrays.asList(testTrainer);
        when(mockEm.createQuery(anyString(), eq(Staff.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("role"), any(StaffRole.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedTrainers);
        
        
        Optional<Staff> result = staffDao.findTrainerByClientId(1L);
        
        
        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
        verify(mockEm).createQuery(anyString(), eq(Staff.class));
        verify(mockTypedQuery).setParameter("clientId", 1L);
        verify(mockTypedQuery).setParameter("role", StaffRole.TRAINER);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindTrainerByClientId_NotFound() {
        
        when(mockEm.createQuery(anyString(), eq(Staff.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("role"), any(StaffRole.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        Optional<Staff> result = staffDao.findTrainerByClientId(999L);
        
        
        assertFalse(result.isPresent());
        verify(mockEm).createQuery(anyString(), eq(Staff.class));
        verify(mockTypedQuery).setParameter("clientId", 999L);
        verify(mockTypedQuery).setParameter("role", StaffRole.TRAINER);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindTrainers_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Staff.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("role"), any(StaffRole.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Staff> result = staffDao.findTrainers();
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Staff.class));
        verify(mockTypedQuery).setParameter("role", StaffRole.TRAINER);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindActiveTrainers_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Staff.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("role"), any(StaffRole.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Staff> result = staffDao.findActiveTrainers();
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Staff.class));
        verify(mockTypedQuery).setParameter("role", StaffRole.TRAINER);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
}
