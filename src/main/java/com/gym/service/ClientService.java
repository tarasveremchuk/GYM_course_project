package com.gym.service;

import com.gym.model.Client;
import com.gym.model.ClientStatus;
import com.gym.model.User;
import com.gym.dao.impl.ClientDao;
import com.gym.dao.impl.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;


@Slf4j
public class ClientService {
    
    private final ClientDao clientDao;
    private final UserDao userDao;
    
    public ClientService() {
        this.clientDao = new ClientDao();
        this.userDao = new UserDao();
    }
    

    public ClientService(ClientDao clientDao, UserDao userDao) {
        this.clientDao = clientDao;
        this.userDao = userDao;
    }
    

    public List<Client> getAllClients() {
        log.info("Retrieving all clients");
        return clientDao.findAll();
    }
    

    public Optional<Client> getClientById(Long id) {
        log.info("Finding client with ID: {}", id);
        return clientDao.findById(id);
    }
    

    public List<Client> getClientsByStatus(ClientStatus status) {
        log.info("Finding clients with status: {}", status);
        return clientDao.findByStatus(status);
    }
    

    public Client getClientByUserId(Long userId) {
        log.info("Finding client with user ID: {}", userId);
        return clientDao.findByUserId(userId);
    }

    public List<Client> getClientsByTrainerId(Long trainerId) {
        log.info("Finding clients with trainer ID: {}", trainerId);
        return clientDao.findByTrainerId(trainerId);
    }
    

    public List<Client> searchClientsByName(String name) {
        log.info("Searching for clients with name containing: {}", name);
        return clientDao.searchByName(name);
    }
    

    public Client saveClient(Client client, String username, String password) throws Exception {
        if (client.getId() == null) {
            log.info("Creating new client: {}", client.getFullName());
            
            Client savedClient = clientDao.save(client);
            if (savedClient == null) {
                throw new Exception("Failed to save client");
            }
            
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
            user.setRole(User.UserRole.client);
            user.setClientId(savedClient.getId());
            
            log.info("Creating user account for client ID: {}", savedClient.getId());
            user.setClientId(savedClient.getId());
            User savedUser = userDao.save(user);
            if (savedUser == null) {
                throw new Exception("Failed to create user account for client");
            }
            
            if (savedClient.getUser() == null) {
                savedClient.setUser(savedUser);
                savedClient = clientDao.save(savedClient);
            }
            
            clientDao.initializeClientProperties(savedClient);
            
            return savedClient;
        } else {
            log.info("Updating existing client ID: {}", client.getId());
            Client savedClient = clientDao.save(client);
            if (savedClient == null) {
                throw new Exception("Failed to update client");
            }
            
            clientDao.initializeClientProperties(savedClient);
            
            return savedClient;
        }
    }
    

    public void deleteClient(Long id) throws Exception {
        log.info("Deleting client with ID: {}", id);
        clientDao.deleteById(id);
    }
    

    public void initializeClientProperties(Client client) {
        clientDao.initializeClientProperties(client);
    }
}
