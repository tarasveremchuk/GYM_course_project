package com.gym.service;

import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.UserDao;
import com.gym.model.Client;
import com.gym.model.ClientStatus;
import com.gym.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientDao clientDao;
    
    @Mock
    private UserDao userDao;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Test Client");
        testClient.setEmail("test@example.com");
        testClient.setPhone("+380501234567");
        testClient.setBirthDate(LocalDate.of(1990, 1, 1));
        testClient.setStatus(ClientStatus.ACTIVE);
    }

    @Test
    void getAllClients_ShouldReturnAllClients() {
        
        List<Client> expectedClients = Arrays.asList(testClient, new Client());
        when(clientDao.findAll()).thenReturn(expectedClients);

        
        List<Client> actualClients = clientService.getAllClients();

        
        assertEquals(expectedClients.size(), actualClients.size());
        assertEquals(expectedClients, actualClients);
        verify(clientDao).findAll();
    }

    @Test
    void getClientsByStatus_ShouldReturnClientsWithSpecifiedStatus() {
        
        List<Client> expectedClients = Arrays.asList(testClient);
        when(clientDao.findByStatus(any(ClientStatus.class))).thenReturn(expectedClients);

        
        List<Client> actualClients = clientService.getClientsByStatus(ClientStatus.ACTIVE);

        
        assertEquals(expectedClients.size(), actualClients.size());
        assertEquals(expectedClients, actualClients);
        verify(clientDao).findByStatus(ClientStatus.ACTIVE);
    }

    @Test
    void getClientById_WhenClientExists_ShouldReturnClient() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.of(testClient));

        
        Optional<Client> result = clientService.getClientById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(testClient, result.get());
        verify(clientDao).findById(1L);
    }

    @Test
    void getClientById_WhenClientDoesNotExist_ShouldReturnEmptyOptional() {
        
        when(clientDao.findById(anyLong())).thenReturn(Optional.empty());

        
        Optional<Client> result = clientService.getClientById(999L);

        
        assertFalse(result.isPresent());
        verify(clientDao).findById(999L);
    }

    @Test
    void saveClient_NewClient_ShouldSaveAndReturnClient() throws Exception {
        
        testClient.setId(null); 
        when(clientDao.save(any(Client.class))).thenReturn(testClient);
        when(userDao.save(any(User.class))).thenReturn(new User());

        
        Client result = clientService.saveClient(testClient, "testuser", "password");

        
        assertNotNull(result);
        assertEquals(testClient, result);
        verify(clientDao, times(2)).save(any(Client.class)); 
        verify(userDao).save(any(User.class));
    }

    @Test
    void saveClient_ExistingClient_ShouldUpdateAndReturnClient() throws Exception {
        
        when(clientDao.save(any(Client.class))).thenReturn(testClient);

        
        Client result = clientService.saveClient(testClient, null, null);

        
        assertNotNull(result);
        assertEquals(testClient, result);
        verify(clientDao).save(testClient);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void saveClient_WhenSaveFails_ShouldThrowException() {
        
        testClient.setId(null); 
        when(clientDao.save(any(Client.class))).thenReturn(null);

        
        Exception exception = assertThrows(Exception.class, () -> {
            clientService.saveClient(testClient, "testuser", "password");
        });
        
        assertEquals("Failed to save client", exception.getMessage());
        verify(clientDao).save(testClient);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void getClientByUserId_ShouldReturnClient() {
        
        when(clientDao.findByUserId(anyLong())).thenReturn(testClient);

        
        Client result = clientService.getClientByUserId(1L);

        
        assertNotNull(result);
        assertEquals(testClient, result);
        verify(clientDao).findByUserId(1L);
    }

    @Test
    void getClientsByTrainerId_ShouldReturnClientsList() {
        
        List<Client> expectedClients = Arrays.asList(testClient);
        when(clientDao.findByTrainerId(anyLong())).thenReturn(expectedClients);

        
        List<Client> result = clientService.getClientsByTrainerId(1L);

        
        assertEquals(expectedClients.size(), result.size());
        assertEquals(expectedClients, result);
        verify(clientDao).findByTrainerId(1L);
    }

    @Test
    void deleteClient_ShouldCallDaoDeleteMethod() throws Exception {
        
        doNothing().when(clientDao).deleteById(anyLong());

        
        clientService.deleteClient(1L);

        
        verify(clientDao).deleteById(1L);
    }

    @Test
    void deleteClient_WhenDaoThrowsException_ShouldPropagateException() {
        
        doThrow(new RuntimeException("Database error")).when(clientDao).deleteById(anyLong());

        
        Exception exception = assertThrows(Exception.class, () -> {
            clientService.deleteClient(1L);
        });
        
        verify(clientDao).deleteById(1L);
    }

    @Test
    void searchClientsByName_ShouldReturnMatchingClients() {
        
        String query = "Test";
        List<Client> expectedClients = Arrays.asList(testClient);
        when(clientDao.searchByName(query)).thenReturn(expectedClients);

        
        List<Client> result = clientService.searchClientsByName(query);

        
        assertEquals(expectedClients.size(), result.size());
        assertEquals(expectedClients, result);
        verify(clientDao).searchByName(query);
    }
    
    @Test
    void initializeClientProperties_ShouldCallDaoMethod() {
        
        doNothing().when(clientDao).initializeClientProperties(any(Client.class));
        
        
        clientService.initializeClientProperties(testClient);
        
        
        verify(clientDao).initializeClientProperties(testClient);
    }
}
