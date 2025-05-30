package com.gym.dao.impl;

import com.gym.model.Client;
import com.gym.model.Visit;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VisitDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<Visit> mockTypedQuery;
    
    @Spy
    @InjectMocks
    private VisitDao visitDao;
    
    private Visit testVisit;
    private Client testClient;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("John Doe");
        
        
        testVisit = new Visit();
        testVisit.setId(1L);
        testVisit.setClient(testClient);
        testVisit.setVisitDate(LocalDateTime.now());
        testVisit.setNotes("Regular visit");
        
        
        doReturn(mockEm).when(visitDao).getEntityManager();
    }
    
    @Test
    void testFindByClientId() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedVisits);
        
        
        List<Visit> result = visitDao.findByClientId(1L);
        
        
        assertEquals(expectedVisits, result);
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setParameter("clientId", 1L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByBookingId_Found() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("bookingId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedVisits);
        
        
        Optional<Visit> result = visitDao.findByBookingId(1L);
        
        
        assertTrue(result.isPresent());
        assertEquals(testVisit, result.get());
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setParameter("bookingId", 1L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByBookingId_NotFound() {
        
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("bookingId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        Optional<Visit> result = visitDao.findByBookingId(999L);
        
        
        assertFalse(result.isPresent());
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setParameter("bookingId", 999L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindVisitsByMonth() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("year"), anyInt())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("month"), anyInt())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedVisits);
        
        
        List<Visit> result = visitDao.findVisitsByMonth(2023, 5);
        
        
        assertEquals(expectedVisits, result);
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setParameter("year", 2023);
        verify(mockTypedQuery).setParameter("month", 5);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindRecentVisits() {
        
        List<Visit> expectedVisits = Arrays.asList(testVisit);
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(anyInt())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedVisits);
        
        
        List<Visit> result = visitDao.findRecentVisits(10);
        
        
        assertEquals(expectedVisits, result);
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setMaxResults(10);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByClientId_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Visit> result = visitDao.findByClientId(999L);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setParameter("clientId", 999L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindVisitsByMonth_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("year"), anyInt())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("month"), anyInt())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Visit> result = visitDao.findVisitsByMonth(2023, 12);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setParameter("year", 2023);
        verify(mockTypedQuery).setParameter("month", 12);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindRecentVisits_EmptyResult() {
        
        when(mockEm.createQuery(anyString(), eq(Visit.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setMaxResults(anyInt())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Visit> result = visitDao.findRecentVisits(10);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Visit.class));
        verify(mockTypedQuery).setMaxResults(10);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
}
