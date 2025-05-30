package com.gym.dao.impl;

import com.gym.model.Client;
import com.gym.model.Membership;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembershipDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<Membership> mockTypedQuery;
    
    @Spy
    @InjectMocks
    private MembershipDao membershipDao;
    
    private Membership testMembership;
    private Client testClient;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("John Doe");
        
        
        testMembership = new Membership();
        testMembership.setId(1L);
        testMembership.setClient(testClient);
        testMembership.setType(Membership.MembershipType.unlimited);
        testMembership.setStartDate(LocalDate.now());
        testMembership.setEndDate(LocalDate.now().plusMonths(1));
        testMembership.setPaid(true);
        testMembership.setPrice(1000.0);
        
        
        doReturn(mockEm).when(membershipDao).getEntityManager();
    }
    
    @Test
    void testFindActiveByClientId() {
        
        List<Membership> expectedMemberships = Arrays.asList(testMembership);
        when(mockEm.createQuery(anyString(), eq(Membership.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("currentDate"), any(LocalDate.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedMemberships);
        
        
        List<Membership> result = membershipDao.findActiveByClientId(1L);
        
        
        assertEquals(expectedMemberships, result);
        verify(mockEm).createQuery(anyString(), eq(Membership.class));
        verify(mockTypedQuery).setParameter("clientId", 1L);
        verify(mockTypedQuery).setParameter(eq("currentDate"), any(LocalDate.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindExpiringMemberships() {
        
        LocalDate beforeDate = LocalDate.now().plusDays(7);
        List<Membership> expectedMemberships = Arrays.asList(testMembership);
        when(mockEm.createQuery(anyString(), eq(Membership.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("beforeDate"), any(LocalDate.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("currentDate"), any(LocalDate.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedMemberships);
        
        
        List<Membership> result = membershipDao.findExpiringMemberships(beforeDate);
        
        
        assertEquals(expectedMemberships, result);
        verify(mockEm).createQuery(anyString(), eq(Membership.class));
        verify(mockTypedQuery).setParameter("beforeDate", beforeDate);
        verify(mockTypedQuery).setParameter(eq("currentDate"), any(LocalDate.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByType() {
        
        List<Membership> expectedMemberships = Arrays.asList(testMembership);
        when(mockEm.createQuery(anyString(), eq(Membership.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("type"), any(Membership.MembershipType.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedMemberships);
        
        
        List<Membership> result = membershipDao.findByType(Membership.MembershipType.unlimited);
        
        
        assertEquals(expectedMemberships, result);
        verify(mockEm).createQuery(anyString(), eq(Membership.class));
        verify(mockTypedQuery).setParameter("type", Membership.MembershipType.unlimited);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindActiveByClientId_NoResults() {
        
        when(mockEm.createQuery(anyString(), eq(Membership.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("clientId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("currentDate"), any(LocalDate.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Membership> result = membershipDao.findActiveByClientId(1L);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Membership.class));
        verify(mockTypedQuery).setParameter("clientId", 1L);
        verify(mockTypedQuery).setParameter(eq("currentDate"), any(LocalDate.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindExpiringMemberships_NoResults() {
        
        LocalDate beforeDate = LocalDate.now().plusDays(7);
        when(mockEm.createQuery(anyString(), eq(Membership.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("beforeDate"), any(LocalDate.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("currentDate"), any(LocalDate.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Membership> result = membershipDao.findExpiringMemberships(beforeDate);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Membership.class));
        verify(mockTypedQuery).setParameter("beforeDate", beforeDate);
        verify(mockTypedQuery).setParameter(eq("currentDate"), any(LocalDate.class));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByType_NoResults() {
        
        when(mockEm.createQuery(anyString(), eq(Membership.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("type"), any(Membership.MembershipType.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(List.of());
        
        
        List<Membership> result = membershipDao.findByType(Membership.MembershipType.limited);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Membership.class));
        verify(mockTypedQuery).setParameter("type", Membership.MembershipType.limited);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
}
