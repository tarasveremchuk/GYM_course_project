package com.gym.dao.impl;

import com.gym.model.Client;
import com.gym.model.ClientStatus;
import com.gym.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
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
class ClientDaoTest {

    @Mock
    private EntityManagerFactory mockEmf;
    
    @Mock
    private EntityManager mockEm;
    
    @Mock
    private EntityTransaction mockTransaction;
    
    @Mock
    private TypedQuery<Client> mockTypedQuery;
    
    @Mock
    private Query mockNativeQuery;
    
    @Spy
    @InjectMocks
    private ClientDao clientDao;
    
    private Client testClient;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        
        when(mockEm.getTransaction()).thenReturn(mockTransaction);
        
        
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("John Doe");
        testClient.setBirthDate(LocalDate.of(1990, 1, 15));
        testClient.setPhone("+380501234567");
        testClient.setEmail("john.doe@example.com");
        testClient.setStatus(ClientStatus.ACTIVE);
        
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("johndoe");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.UserRole.client);
        testUser.setClientId(1L);
        
        testClient.setUser(testUser);
        
        
        doReturn(mockEm).when(clientDao).getEntityManager();
    }
    
    @Test
    void testFindAll() {
        
        List<Client> expectedClients = Arrays.asList(testClient);
        
        jakarta.persistence.criteria.CriteriaBuilder mockCriteriaBuilder = mock(jakarta.persistence.criteria.CriteriaBuilder.class);
        jakarta.persistence.criteria.CriteriaQuery<Client> mockCriteriaQuery = mock(jakarta.persistence.criteria.CriteriaQuery.class);
        jakarta.persistence.TypedQuery<Client> mockQuery = mock(jakarta.persistence.TypedQuery.class);
        
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Client.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Client.class)).thenReturn(mock(jakarta.persistence.criteria.Root.class));
        when(mockEm.createQuery(mockCriteriaQuery)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedClients);
        
        
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        List<Client> result = clientDao.findAll();
        
        
        assertEquals(expectedClients, result);
        verify(clientDao).initializeClientProperties(testClient);
        verify(mockEm).close();
    }
    
    @Test
    void testFindById() {
        
        when(mockEm.find(Client.class, 1L)).thenReturn(testClient);
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        java.util.Optional<Client> result = clientDao.findById(1L);
        
        
        assertTrue(result.isPresent());
        assertEquals(testClient, result.get());
        verify(clientDao).initializeClientProperties(testClient);
        verify(mockEm).close();
    }
    
    @Test
    void testFindByStatus() {
        
        List<Client> expectedClients = Arrays.asList(testClient);
        when(mockEm.createQuery(anyString(), eq(Client.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("status"), any(ClientStatus.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedClients);
        
        
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        List<Client> result = clientDao.findByStatus(ClientStatus.ACTIVE);
        
        
        assertEquals(expectedClients, result);
        verify(mockEm).createQuery(anyString(), eq(Client.class));
        verify(mockTypedQuery).setParameter("status", ClientStatus.ACTIVE);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
        verify(clientDao).initializeClientProperties(testClient);
    }
    
    @Test
    void testFindByUserId_Success() {
        
        when(mockEm.createNativeQuery(anyString())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.setParameter(eq("userId"), anyLong())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.getSingleResult()).thenReturn(1L);
        when(mockEm.find(Client.class, 1L)).thenReturn(testClient);
        
        
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        Client result = clientDao.findByUserId(1L);
        
        
        assertNotNull(result);
        assertEquals(testClient, result);
        verify(mockEm).createNativeQuery(anyString());
        verify(mockNativeQuery).setParameter("userId", 1L);
        verify(mockNativeQuery).getSingleResult();
        verify(mockEm).find(Client.class, 1L);
        verify(mockEm).close();
        verify(clientDao).initializeClientProperties(testClient);
    }
    
    @Test
    void testFindByUserId_NoClientId() {
        
        when(mockEm.createNativeQuery(anyString())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.setParameter(eq("userId"), anyLong())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.getSingleResult()).thenReturn(null);
        
        
        Client result = clientDao.findByUserId(1L);
        
        
        assertNull(result);
        verify(mockEm).createNativeQuery(anyString());
        verify(mockNativeQuery).setParameter("userId", 1L);
        verify(mockNativeQuery).getSingleResult();
        verify(mockEm, never()).find(eq(Client.class), anyLong());
        verify(mockEm).close();
    }
    
    @Test
    void testFindByUserId_NoClientFound() {
        
        when(mockEm.createNativeQuery(anyString())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.setParameter(eq("userId"), anyLong())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.getSingleResult()).thenReturn(1L);
        when(mockEm.find(Client.class, 1L)).thenReturn(null);
        
        
        Client result = clientDao.findByUserId(1L);
        
        
        assertNull(result);
        verify(mockEm).createNativeQuery(anyString());
        verify(mockNativeQuery).setParameter("userId", 1L);
        verify(mockNativeQuery).getSingleResult();
        verify(mockEm).find(Client.class, 1L);
        verify(mockEm).close();
    }
    
    @Test
    void testFindByUserId_Exception() {
        
        when(mockEm.createNativeQuery(anyString())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.setParameter(eq("userId"), anyLong())).thenReturn(mockNativeQuery);
        when(mockNativeQuery.getSingleResult()).thenThrow(new RuntimeException("Test exception"));
        
        
        Client result = clientDao.findByUserId(1L);
        
        
        assertNull(result);
        verify(mockEm).createNativeQuery(anyString());
        verify(mockNativeQuery).setParameter("userId", 1L);
        verify(mockNativeQuery).getSingleResult();
        verify(mockEm).close();
    }
    
    @Test
    void testFindByTrainerId() {
        
        List<Client> expectedClients = Arrays.asList(testClient);
        when(mockEm.createQuery(anyString(), eq(Client.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("trainerId"), anyLong())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedClients);
        
        
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        List<Client> result = clientDao.findByTrainerId(1L);
        
        
        assertEquals(expectedClients, result);
        verify(mockEm).createQuery(anyString(), eq(Client.class));
        verify(mockTypedQuery).setParameter("trainerId", 1L);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
        verify(clientDao).initializeClientProperties(testClient);
    }
    
    @Test
    void testSearchByName() {
        
        List<Client> expectedClients = Arrays.asList(testClient);
        when(mockEm.createQuery(anyString(), eq(Client.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("name"), anyString())).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(expectedClients);
        
        
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        List<Client> result = clientDao.searchByName("John");
        
        
        assertEquals(expectedClients, result);
        verify(mockEm).createQuery(anyString(), eq(Client.class));
        verify(mockTypedQuery).setParameter(eq("name"), eq("%John%"));
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
        verify(clientDao).initializeClientProperties(testClient);
    }
    
    @Test
    void testSave_NewClient() {
        
        Client newClient = new Client();
        newClient.setFullName("Jane Smith");
        newClient.setUser(testUser);
        
        
        clientDao.save(newClient);
        
        
        verify(mockTransaction).begin();
        verify(mockEm).persist(newClient);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testSave_ExistingClient() {
        
        when(mockEm.merge(testClient)).thenReturn(testClient);
        
        
        Client result = clientDao.save(testClient);
        
        
        assertEquals(testClient, result);
        verify(mockTransaction).begin();
        verify(mockEm).merge(testClient);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testSave_Exception() {
        
        doThrow(new RuntimeException("Test exception")).when(mockEm).persist(any());
        when(mockTransaction.isActive()).thenReturn(true);
        
        
        Client result = clientDao.save(new Client());
        
        
        assertNull(result);
        verify(mockTransaction).begin();
        verify(mockTransaction).rollback();
        verify(mockEm).close();
    }
    
    @Test
    void testInitializeClientProperties_Success() {
        
        Client client = new Client();
        client.setId(1L);
        client.setFullName("Test Client");
        
        
        clientDao.initializeClientProperties(client);
        
        
        
    }
    
    @Test
    void testInitializeClientProperties_Exception() {
        
        
        
        
        Client client = new Client();
        
        
        clientDao.initializeClientProperties(client);
        
        
        
        
    }
    
    @Test
    void testSave_NullUser() {
        
        Client client = new Client();
        client.setId(null); 
        client.setFullName("Test Client");
        client.setUser(null); 
        
        
        Client savedClient = clientDao.save(client);
        
        
        assertSame(client, savedClient);
        verify(mockTransaction).begin();
        verify(mockEm).persist(client);
        verify(mockTransaction).commit();
        verify(mockEm).close();
    }
    
    @Test
    void testFindAll_EmptyList() {
        
        List<Client> emptyList = List.of();
        
        jakarta.persistence.criteria.CriteriaBuilder mockCriteriaBuilder = mock(jakarta.persistence.criteria.CriteriaBuilder.class);
        jakarta.persistence.criteria.CriteriaQuery<Client> mockCriteriaQuery = mock(jakarta.persistence.criteria.CriteriaQuery.class);
        jakarta.persistence.TypedQuery<Client> mockQuery = mock(jakarta.persistence.TypedQuery.class);
        
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCriteriaBuilder);
        when(mockCriteriaBuilder.createQuery(Client.class)).thenReturn(mockCriteriaQuery);
        when(mockCriteriaQuery.from(Client.class)).thenReturn(mock(jakarta.persistence.criteria.Root.class));
        when(mockEm.createQuery(mockCriteriaQuery)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(emptyList);
        
        
        List<Client> result = clientDao.findAll();
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).close();
    }
    
    @Test
    void testFindByStatus_EmptyList() {
        
        List<Client> emptyList = List.of();
        when(mockEm.createQuery(anyString(), eq(Client.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.setParameter(eq("status"), any(ClientStatus.class))).thenReturn(mockTypedQuery);
        when(mockTypedQuery.getResultList()).thenReturn(emptyList);
        
        
        List<Client> result = clientDao.findByStatus(ClientStatus.INACTIVE);
        
        
        assertTrue(result.isEmpty());
        verify(mockEm).createQuery(anyString(), eq(Client.class));
        verify(mockTypedQuery).setParameter("status", ClientStatus.INACTIVE);
        verify(mockTypedQuery).getResultList();
        verify(mockEm).close();
    }
}
